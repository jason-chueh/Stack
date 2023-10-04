package com.example.stack.home

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stack.NavigationDirections
import com.example.stack.databinding.FragmentHomeBinding
import com.example.stack.login.UserManager
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class HomeFragment: Fragment() {
    lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by viewModels()
    var imageUri: Uri? = null
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.fab.setOnClickListener{
            findNavController().navigate(NavigationDirections.navigateToTemplateFragment())
        }
        viewModel.upsertTemplate()
//        viewModel.searchTemplateByUserId()
        viewModel.upsertTemplateExerciseRecordList()
//        viewModel.exerciseApi()
        viewModel.createChatroom()
//        viewModel.exerciseApiRe()

        val layoutManager = LinearLayoutManager(this.requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val adapter = HomeWorkoutHistoryAdapter()
        binding.homeWorkoutRecyclerView.layoutManager = layoutManager
        binding.homeWorkoutRecyclerView.adapter = adapter
        viewModel.getUserWorkoutData()
        viewModel.getUserExerciseData()

        binding.url = UserManager.user?.picture

        viewModel.userWorkoutRecords.observe(viewLifecycleOwner){
            if(it!=null){
                binding.totalWorkoutNum.text = it.size.toString()
                if(it.size > 0){
                    binding.previousWorkoutName.text = it.sortedByDescending{ it.startTime }[0].workoutName
                    binding.datePreviousWorkout.text = formatTimestamp(it.sortedByDescending{ it.startTime }[0].startTime)
                }
                else{
                    binding.previousWorkoutName.text = "No workout history"
                    binding.datePreviousWorkout.text = "?"
                }
                adapter.submitList(it.sortedByDescending { it.startTime })
            }
        }
        viewModel.userExerciseRecords.observe(viewLifecycleOwner){

        }



        binding.personalImage.setOnClickListener {
            checkImagePermission()
            pickImageFromGallery()
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

        intent.setType("image/*")
        intent.setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE)
    }

    private fun showPermissionRationaleDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Album Permission Required")
            .setMessage("This app need use your album")
            .setPositiveButton("Grant") { dialog, _ ->
                dialog.dismiss()
                (true)
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
    fun formatTimestamp(timestamp: Long): String {
        val dateFormat = SimpleDateFormat("d'\n' MMM", Locale.getDefault())
        return dateFormat.format(Date(timestamp))
    }
}