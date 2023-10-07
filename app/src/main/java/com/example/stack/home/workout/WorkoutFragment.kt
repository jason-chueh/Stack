package com.example.stack.home.workout

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.cardview.widget.CardView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.stack.NavigationDirections
import com.example.stack.R
import com.example.stack.data.dataclass.Exercise
import com.example.stack.data.dataclass.RepsAndWeightsWithCheck
import com.example.stack.databinding.DialogSaveTemplateBinding
import com.example.stack.databinding.FragmentWorkoutBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class WorkoutFragment : Fragment(), ExerciseDialog.ExerciseDialogListener {
    @Inject
    lateinit var factory: WorkoutViewModel.Factory

    private val viewModel: WorkoutViewModel by activityViewModels {
        WorkoutViewModel.provideWorkoutViewModelFactory(factory, "test")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val templateExerciseList =
            WorkoutFragmentArgs.fromBundle(requireArguments()).templateExercises?.toList()
        if (templateExerciseList != null) {
            viewModel.setDataListFromBundle(templateExerciseList)
        }
        Log.i("template", "$templateExerciseList")
        super.onCreate(savedInstanceState)
    }

    lateinit var binding: FragmentWorkoutBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWorkoutBinding.inflate(inflater, container, false)
        Log.i("workout", "$viewModel")
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        val adapter = WorkoutAdapter(
            { exerciseName, exerciseId ->
                findNavController().navigate(
                    NavigationDirections.navigateToExerciseDetailFragment(
                        exerciseId,
                        exerciseName
                    )
                )
            },
            { exercisePosition, setPosition, repsAndWeights ->
                updateSetToTrue(exercisePosition, setPosition, repsAndWeights)
//                val layoutManager = binding.workoutRecyclerView.layoutManager as LinearLayoutManager
//                layoutManager.scrollToPositionWithOffset(2, 0) // 滚动到第二个项目
//                val innerRecyclerView = layoutManager.findViewByPosition(2) as RecyclerView
//                val innerLayoutManager = innerRecyclerView.layoutManager as LinearLayoutManager
//                innerLayoutManager.scrollToPositionWithOffset(1, 0)
            },
            viewModel.updateSetToFalse,
            viewModel.addSet,
            viewModel.expandExercise,
            viewModel.deleteExerciseRecord
        )

        viewModel.notifyItemChangePosition.observe(viewLifecycleOwner) {
            adapter.notifyItemChanged(it)
        }

        binding.workoutRecyclerView.adapter = adapter


//        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver(){
//            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
//                binding.workoutRecyclerView.scrollToPosition(itemCount)
//            }
//        })

        viewModel.getAllExerciseFromDb()

        viewModel.dataList.observe(viewLifecycleOwner) {
            Log.i("workout", "$it")
            adapter.submitList(it)
        }

        viewModel.scrollToPosition.observe(viewLifecycleOwner) {
//            binding.workoutRecyclerView.scrollToPosition(adapter.itemCount - 1)
            Log.i("scroll", "$it")
            binding.workoutRecyclerView.scrollToPosition(it)
        }

        viewModel.scrollToInnerPosition.observe(viewLifecycleOwner) {
            Log.i("scroll", "$it")

            if (it != null) {
                Log.i("scroll", "$it")
                val layoutManager = binding.workoutRecyclerView.layoutManager as LinearLayoutManager
                layoutManager.scrollToPositionWithOffset(it.first, 0)
                val cardView = layoutManager.findViewByPosition(it.first) as? CardView
                if (cardView != null) {
                    val innerRecyclerView =
                        cardView.findViewById<RecyclerView>(R.id.repsRecyclerView)
                    if (innerRecyclerView != null) {
                        val innerLayoutManager =
                            innerRecyclerView.layoutManager as LinearLayoutManager
                        innerLayoutManager.scrollToPositionWithOffset(it.second, 0)
                    } else {
                        Log.i("scroll", "innerRecyclerView not found")
                        // Handle the case where the innerRecyclerView is not found
                    }
                } else {
                    Log.i("scroll", "error! cardView not found")
                }
            }
        }

        binding.addExercise.setOnClickListener {
            val dialog = ExerciseDialog()
            dialog.setExerciseDialogListener(this)
            dialog.show(childFragmentManager, "EXERCISE_DIALOG_TAG")
//            showExerciseDialog()
        }

        binding.timerIcon.setOnClickListener {
            findNavController().navigate(NavigationDirections.navigateToTimerFragment())
        }

        binding.timerText.setOnClickListener {
            findNavController().navigate(NavigationDirections.navigateToTimerFragment())
        }

        binding.pencil.setOnClickListener {
            binding.workoutTitleText.isEnabled = !binding.workoutTitleText.isEnabled
        }

        binding.finishWorkoutText.setOnClickListener {
            showSaveAsTemplateDialog()
        }

        binding.cancel.setOnClickListener {
            findNavController().navigate(NavigationDirections.navigateToTemplateFragment())
            viewModel.cancelWorkout()
        }


        return binding.root
    }

    private fun showSaveAsTemplateDialog() {
        val dialog = Dialog(this.requireContext())
        val dialogBinding: DialogSaveTemplateBinding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.dialog_save_template,
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

        dialogBinding.continueText.setOnClickListener {
            if (!binding.workoutTitleText.text.isNullOrBlank()) {
                viewModel.finishWorkoutWithSaveTemplate(binding.workoutTitleText.text.toString())
                dialog.dismiss()
                findNavController().navigate(NavigationDirections.navigateToTemplateFragment())
            }
        }

        dialogBinding.rejectText.setOnClickListener {
            if (!binding.workoutTitleText.text.isNullOrBlank()) {
                viewModel.finishWorkoutWithoutSaveTemplate(binding.workoutTitleText.text.toString())
                dialog.dismiss()
                findNavController().navigate(NavigationDirections.navigateToTemplateFragment())
            }
        }

        dialogBinding.cancelView.setOnClickListener {
            dialog.dismiss()
        }

        dialogBinding.root.setOnClickListener {
            dialog.dismiss()
        }

    }

    private val updateSetToTrue: (exercisePosition: Int, setPosition: Int, repsAndWeights: RepsAndWeightsWithCheck) -> Unit =
        { exercisePosition, setPosition, repsAndWeights ->
            viewModel.updateSetToTrue(exercisePosition, setPosition, repsAndWeights)
            viewModel.findFirstUncheckedPosition()
        }

    override fun onExerciseSelected(position: Int) {
        Log.i("terry", "$position")
    }
}

val exerciseList = listOf(
    Exercise(
        id = "0026",
        name = "barbell bench squat",
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