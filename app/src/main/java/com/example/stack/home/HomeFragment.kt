package com.example.stack.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stack.NavigationDirections
import com.example.stack.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class HomeFragment: Fragment() {
    lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by viewModels()
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

        val layoutManager = LinearLayoutManager(this.requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val adapter = HomeWorkoutHistoryAdapter()
        binding.homeWorkoutRecyclerView.layoutManager = layoutManager
        binding.homeWorkoutRecyclerView.adapter = adapter
        viewModel.getUserWorkoutData()
        viewModel.getUserExerciseData()

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
                adapter.submitList(it)
            }
        }









        return binding.root
    }
    fun formatTimestamp(timestamp: Long): String {
        val dateFormat = SimpleDateFormat("d'\n' MMM", Locale.getDefault())
        return dateFormat.format(Date(timestamp))
    }
}