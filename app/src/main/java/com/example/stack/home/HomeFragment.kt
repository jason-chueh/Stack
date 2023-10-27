package com.example.stack.home

import android.Manifest
import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.stack.NavigationDirections
import com.example.stack.R
import com.example.stack.data.StackRepository
import com.example.stack.databinding.DialogCancelWorkoutBinding
import com.example.stack.databinding.DialogLogoutBinding
import com.example.stack.databinding.FragmentHomeBinding
import com.example.stack.home.template.ChooseTemplateDialog
import com.example.stack.login.UserManager
import com.google.android.material.carousel.CarouselLayoutManager
import com.google.android.material.carousel.CarouselSnapHelper
import com.google.android.material.carousel.FullScreenCarouselStrategy
import com.google.android.material.carousel.HeroCarouselStrategy
import com.google.android.material.carousel.MultiBrowseCarouselStrategy
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {
    @Inject
    lateinit var userManager: UserManager
    lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by viewModels()
    private var imageUri: Uri? = null

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.fab.setOnClickListener {
//            findNavController().navigate(NavigationDirections.navigateToTemplateFragment())
            if (userManager.isLoggedIn) {
                val chooseTemplateDialog = ChooseTemplateDialog()
                chooseTemplateDialog.show(childFragmentManager, "")
            } else {
                findNavController().navigate(NavigationDirections.navigateToLoginDialog())
            }
        }
        viewModel.exerciseApiToDatabase()
//        viewModel.deleteYoutube()
//        viewModel.deleteAllTemplate()
        viewModel.upsertTemplate()
//        viewModel.searchTemplateByUserId()
        viewModel.upsertTemplateExerciseRecordList()
//        viewModel.exerciseApi()
//        viewModel.createChatroom()
//        viewModel.exerciseApiRe()
//        val layoutManager = LinearLayoutManager(this.requireContext(), LinearLayoutManager.HORIZONTAL, false)
//        val adapter = HomeWorkoutHistoryAdapter()
//        binding.homeWorkoutRecyclerView.layoutManager = layoutManager
//        binding.homeWorkoutRecyclerView.adapter = adapter
        val carouselAdapter = HomeCarouselAdapter()

        binding.carouselRecyclerView.adapter = carouselAdapter

        val carouselLayoutManager =
            CarouselLayoutManager(MultiBrowseCarouselStrategy(), RecyclerView.HORIZONTAL)
        binding.carouselRecyclerView.layoutManager = carouselLayoutManager

        val snapHelper = CarouselSnapHelper()
        snapHelper.attachToRecyclerView(binding.carouselRecyclerView)

        viewModel.getUserWorkoutData()

        viewModel.getUserExerciseData()

        binding.constraintLayout3.setOnClickListener {
            viewModel.animateIcon(binding.imageView6)
        }

//        viewModel.userManager.user?.picture?.let{binding.personalImage.setImageURI(it.toUri())}

        viewModel.userManager.user?.picture?.let{binding.url = it}
        binding.userName.text = if (userManager.user != null) userManager.user!!.name else "Unknown User"
        viewModel.userWorkoutRecords.observe(viewLifecycleOwner) { it ->
            if (it != null) {
                binding.totalWorkoutNum.text = it.size.toString()
                if (it.isNotEmpty()) {
                    binding.carouselRecyclerView.visibility = View.VISIBLE
                    binding.NoWorkoutText.visibility = View.GONE
                    val sortedList = it.sortedByDescending { it.startTime }[0]
                    binding.previousWorkoutName.text = sortedList.workoutName
                    val lastTime = sortedList.startTime
                    binding.datePreviousWorkout.text = formatTimestamp(lastTime)
                    binding.daysAgo.text = getTimeAgo(lastTime)
                } else {
                    binding.carouselRecyclerView.visibility = View.INVISIBLE
                    binding.NoWorkoutText.visibility = View.VISIBLE
                    binding.previousWorkoutName.text = "No workout history"
                    binding.datePreviousWorkout.text = "?"
                    binding.daysAgo.text = "?"
                }
                carouselAdapter.submitList(it.sortedByDescending { it.startTime })

            }
        }
        viewModel.userExerciseRecords.observe(viewLifecycleOwner) {
            val totalWeight = viewModel.getTotalWeight()
            totalWeight?.let {
                binding.totalWeightNum.text = totalWeight.toString()
            }
        }

        binding.personalImage.setOnClickListener {
            checkImagePermission()
            pickImageFromGallery()
        }
        binding.personalImage.setOnLongClickListener {
            showLogoutDialog()
            true
        }

        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                imageUri = data?.data
                if (null != imageUri) {
                    binding.personalImage.setImageURI(imageUri)
                    viewModel.uploadImageToFireStorage(imageUri.toString())
                }
            }
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE)
    }

    private fun showPermissionRationaleDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Album Permission Required")
            .setMessage("This app need use your album")
            .setPositiveButton("Grant") { dialog, _ ->
                dialog.dismiss()
            }
            .setNegativeButton("Deny") { dialog, _ ->
                dialog.dismiss()
                onImageReadPermissionDenied()
            }
            .show()
    }

    private fun requestReadImagesPermission(dialogShown: Boolean = false) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(), READ_IMAGE_PERMISSION
            ) &&
            !dialogShown
        ) {
            showPermissionRationaleDialog()
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(
                    READ_IMAGE_PERMISSION
                ), READ_IMAGE_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun onImageReadPermissionDenied() {
        Toast.makeText(requireContext(), "Camera and Audio Permission Denied", Toast.LENGTH_LONG)
            .show()
    }

    @RequiresApi(Build.VERSION_CODES.S)
    fun checkImagePermission() {
        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                READ_IMAGE_PERMISSION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestReadImagesPermission()
        } else {
            pickImageFromGallery()
        }
    }

    companion object {
        private const val READ_IMAGE_PERMISSION_REQUEST_CODE = 1
        private const val READ_IMAGE_PERMISSION = Manifest.permission.READ_EXTERNAL_STORAGE
        private const val SELECT_PICTURE = 200

    }

    private fun formatTimestamp(timestamp: Long): String {
        val dateFormat = SimpleDateFormat("d'\n' MMM", Locale.getDefault())
        return dateFormat.format(Date(timestamp))
    }

    private fun showLogoutDialog() {
        val dialog = Dialog(this.requireContext())
        val dialogBinding: DialogLogoutBinding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.dialog_logout,
            null,
            false
        )
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(dialogBinding.root)
        dialog.show()
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogBinding.lifecycleOwner = viewLifecycleOwner

        dialogBinding.rejectText.setOnClickListener {
            dialog.dismiss()
        }
        dialogBinding.continueText.setOnClickListener {
            userManager.clear()
            findNavController().navigate(NavigationDirections.navigateToHomeFragment())
        }
        dialogBinding.root.setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun getTimeAgo(timestamp: Long): String {
        val currentTimeMillis = System.currentTimeMillis()
        val timeDifferenceMillis = currentTimeMillis - timestamp
        val minutes = TimeUnit.MILLISECONDS.toMinutes(timeDifferenceMillis)
        val hours = TimeUnit.MILLISECONDS.toHours(timeDifferenceMillis)
        val days = TimeUnit.MILLISECONDS.toDays(timeDifferenceMillis)
        val months = days / 30
        val years = days / 365
        return when {
            years > 1 -> "$years years"
            months > 1 -> "$months months"
            days > 1 -> "$days days"
            hours > 1 -> "$hours hours"
            minutes > 1 -> "$minutes minutes"
            else -> "seconds"
        }
    }
}