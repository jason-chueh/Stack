package com.example.stack.home.workout

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.stack.NavigationDirections
import com.example.stack.data.dataclass.Exercise
import com.example.stack.databinding.DialogExerciseListBinding
import com.example.stack.databinding.FragmentWorkoutBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class WorkoutFragment: Fragment() {
    @Inject lateinit var factory: WorkoutViewModel.Factory

    private val viewModel: WorkoutViewModel by activityViewModels{
        WorkoutViewModel.provideWorkoutViewModelFactory(factory, "test")
    }


    lateinit var binding: FragmentWorkoutBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWorkoutBinding.inflate(inflater, container, false)
        Log.i("workout","$viewModel")
        val adapter = WorkoutAdapter({ _, _ ->
            findNavController().navigate(NavigationDirections.navigateToExerciseDetailFragment())
        }, viewModel.updateSetToTrue, viewModel.updateSetToFalse ,viewModel.addSet)


        binding.workoutRecyclerView.adapter = adapter


        viewModel.dataList.observe(viewLifecycleOwner){
            Log.i("workout","$it")
            adapter.submitList(it)
        }

        binding.addExercise.setOnClickListener {
            showExerciseDialog()
        }

        binding.timer.setOnClickListener {
            findNavController().navigate(NavigationDirections.navigateToTimerFragment())
        }

        binding.finish.setOnClickListener {

        }
        binding.cancel.setOnClickListener {

        }




        return binding.root
    }

    fun showExerciseDialog() {
        val exerciseBinding = DialogExerciseListBinding.inflate(LayoutInflater.from(this.requireContext()))
        val loginDialog = AlertDialog.Builder(this.requireContext())
            .setView(exerciseBinding.root)
            .create()
        var position: Int = -1
        val adapter = ExerciseAdapter(fun(chosePosition: Int){
            if(chosePosition>=0) {
//                Log.i("workout","add exercise called!!!")
                position = chosePosition
            }
        })
        exerciseBinding.btnSubmit.setOnClickListener {
            if(position>=0) {
                val exercise = exerciseList[position]
                viewModel.addExerciseRecord(exercise.id,exercise.name)
                loginDialog.dismiss()
            }
        }
        exerciseBinding.exerciseRecyclerView.adapter = adapter

        adapter.submitList(exerciseList)

        loginDialog.show()


    }
}
val exerciseList = listOf(
    Exercise(
        id = "1",
        name = "Push-up",
        target = "pectorals",
        gifUrl = "https://example.com/push-up.gif",
        bodyPart = "Upper Body",
        equipment = "None"
    ),
    Exercise(
        id = "2",
        name = "Squat",
        target = "glutes",
        gifUrl = "https://example.com/squat.gif",
        bodyPart = "Lower Body",
        equipment = "None"
    ),
    Exercise(
        id = "3",
        name = "Pull-up",
        target = "upper back",
        gifUrl = "https://example.com/pull-up.gif",
        bodyPart = "Upper Body",
        equipment = "Pull-up bar"
    ),
    Exercise(
        id = "4",
        name = "Plank",
        target = "abs",
        gifUrl = "https://example.com/plank.gif",
        bodyPart = "core",
        equipment = "None"
    ),
    Exercise(
        id = "5",
        name = "Dumbbell Curl",
        target = "biceps",
        gifUrl = "https://example.com/dumbbell-curl.gif",
        bodyPart = "Upper Body",
        equipment = "Dumbbells"
    )
    // Add more exercise entries as needed
)