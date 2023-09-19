package com.example.stack.home.exercisedetail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.stack.NavigationDirections
import com.example.stack.databinding.FragmentExerciseDetailBinding
import com.example.stack.databinding.FragmentHomeBinding
import com.example.stack.home.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ExerciseDetailFragment : Fragment() {
    lateinit var binding: FragmentExerciseDetailBinding
    private val viewModel: ExerciseDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        /**
        //will get exerciseId and exerciseName from ExerciseList page
        */

        viewModel.getVideoList("001","squat")
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentExerciseDetailBinding.inflate(inflater, container, false)

        Glide.with(this)
            .load("https://api.exercisedb.io/image/7FuyHY-AOz1aCQ")
            .into(binding.gifImageView)

        val adapter = ExerciseDetailAdapter{
            findNavController().navigate(NavigationDirections.navigateToInstructionFragment(it))
        }
        binding.recyclerView.adapter = adapter

        viewModel.videoResultList.observe(viewLifecycleOwner){
            Log.i("python","fragment observe: $it")
            adapter.submitList(it)
        }


        return binding.root
    }
}