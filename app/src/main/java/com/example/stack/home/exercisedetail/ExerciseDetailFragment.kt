package com.example.stack.home.exercisedetail

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.stack.NavigationDirections
import com.example.stack.R
import com.example.stack.databinding.FragmentExerciseDetailBinding
import com.example.stack.databinding.FragmentHomeBinding
import com.example.stack.home.HomeViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ExerciseDetailFragment : Fragment() {
    lateinit var binding: FragmentExerciseDetailBinding
    private val viewModel: ExerciseDetailViewModel by viewModels()
    lateinit var exerciseId: String
    lateinit var exerciseName: String
    override fun onCreate(savedInstanceState: Bundle?) {
        /**
        //will get exerciseId and exerciseName from ExerciseList page
        */
        exerciseId = ExerciseDetailFragmentArgs.fromBundle(requireArguments()).exerciseId
        exerciseName = ExerciseDetailFragmentArgs.fromBundle(requireArguments()).exerciseName
        Log.i("exerciseDetail","$exerciseId, $exerciseName")
        viewModel.getExerciseById(exerciseId)
        viewModel.getVideoList(exerciseId,exerciseName)
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentExerciseDetailBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel

        val layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)

        val adapter = ExerciseDetailAdapter{
            findNavController().navigate(NavigationDirections.navigateToInstructionFragment(it))
        }
        binding.youtubeListRecyclerView.adapter = adapter
        binding.youtubeListRecyclerView.layoutManager = layoutManager

        viewModel.videoResultList.observe(viewLifecycleOwner){
            Log.i("python","fragment observe: $it")
            adapter.submitList(it)
        }
        viewModel.exercise.observe(viewLifecycleOwner){
            Log.i("exerciseDetail","$it")
        }
        binding.returnIcon.setOnClickListener{
            findNavController().navigateUp()
        }

        return binding.root
    }
}

fun ChipGroup.addChip(context: Context, label: String){

    Chip(context).apply {

        id = View.generateViewId()

        text = label

        isClickable = true

        isCheckable = true

        setChipSpacingHorizontalResource(R.dimen.dimen_16)

        isCheckedIconVisible = false

        isFocusable = true

        addView(this)

    }

}