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
import androidx.activity.addCallback
import androidx.cardview.widget.CardView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.stack.NavigationDirections
import com.example.stack.R
import com.example.stack.data.dataclass.RepsAndWeightsWithCheck
import com.example.stack.databinding.DialogCancelWorkoutBinding
import com.example.stack.databinding.DialogSaveTemplateBinding
import com.example.stack.databinding.FragmentWorkoutBinding
import com.example.stack.home.workout.timer.TimerDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class WorkoutFragment : Fragment() {
    @Inject
    lateinit var factory: WorkoutViewModel.Factory

    lateinit var binding: FragmentWorkoutBinding

    private val viewModel: WorkoutViewModel by activityViewModels {
        WorkoutViewModel.provideWorkoutViewModelFactory(factory, "123")
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        val templateExerciseList =
            WorkoutFragmentArgs.fromBundle(requireArguments()).templateExercises?.toList()
        if (templateExerciseList != null) {
            viewModel.setDataListFromBundle(templateExerciseList)
        }
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            showCancelDialog()
        }
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWorkoutBinding.inflate(inflater, container, false)
        Log.i("workout", "$viewModel")
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        val adapter = WorkoutAdapter(
            detailInfoClickListener = navigateToExerciseDetailFragment,
            completeSet = updateSetToTrue,
            cancelCompleteSet = viewModel.updateSetToFalse,
            addSetOnClick = viewModel.addSet,
            expandListener = viewModel.expandExercise,
            deleteOnClick = viewModel.deleteExerciseRecord
        )

        binding.workoutRecyclerView.adapter = adapter

        enableSwipeToDelete()

        viewModel.getAllExerciseFromDb()

        viewModel.notifyItemChangePosition.observe(viewLifecycleOwner) {
            adapter.notifyItemChanged(it)
        }

        viewModel.dataList.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        viewModel.scrollToPosition.observe(viewLifecycleOwner) {
            binding.workoutRecyclerView.scrollToPosition(it)
        }

        viewModel.smoothScrollTarget.observe(viewLifecycleOwner) {
            if (it != null) {
                val layoutManager = binding.workoutRecyclerView.layoutManager as LinearLayoutManager
                val cardView =
                    layoutManager.findViewByPosition(it.outerRecyclerViewPosition) as? CardView
                if (cardView != null) {
                    val innerRecyclerView =
                        cardView.findViewById<RecyclerView>(R.id.repsRecyclerView)
                    if (innerRecyclerView != null) {
                        val innerLayoutManager =
                            innerRecyclerView.layoutManager as LinearLayoutManager
                        val height =
                            innerLayoutManager.findViewByPosition(it.innerRecyclerViewPosition)?.height
                        if (height != null) {
                            binding.workoutRecyclerView.smoothScrollBy(0, height)
                        }
                    } else {
                        Log.i("scroll", "innerRecyclerView not found")
                    }
                } else {
                    Log.i("scroll", "cardView not found")
                }
            }
        }

        binding.addExercise.setOnClickListener {
            val dialog = ExerciseDialog()
            dialog.show(childFragmentManager, "EXERCISE_DIALOG_TAG")
        }

        binding.timerIcon.setOnClickListener {
            val dialog = TimerDialogFragment()
            dialog.show(childFragmentManager, "TimerDialog")
        }

        binding.timerText.setOnClickListener {
            val dialog = TimerDialogFragment()
            dialog.show(childFragmentManager, "TimerDialog")
        }

        binding.pencil.setOnClickListener {
            binding.workoutTitleText.isEnabled = !binding.workoutTitleText.isEnabled
        }

        binding.finishWorkoutText.setOnClickListener {
            showSaveAsTemplateDialog()
        }

        binding.cancel.setOnClickListener {
            showCancelDialog()
        }

        return binding.root
    }

    private val navigateToExerciseDetailFragment: (String, String) -> Unit = { exerciseName, exerciseId ->
        findNavController().navigate(
            NavigationDirections.navigateToExerciseDetailFragment(
                exerciseId,
                exerciseName
            )
        )
    }

    private val updateSetToTrue: (exercisePosition: Int, setPosition: Int, repsAndWeights: RepsAndWeightsWithCheck) -> Unit =
        { exercisePosition, setPosition, repsAndWeights ->
            viewModel.updateSetToTrue(exercisePosition, setPosition, repsAndWeights)
            viewModel.calculateScroll(exercisePosition, setPosition)
        }
    
    private fun enableSwipeToDelete() {
        val swipeToDeleteCallback: SwipeToDeleteCallback =
            object : SwipeToDeleteCallback(this.requireContext()) {
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, i: Int) {
                    val position = viewHolder.absoluteAdapterPosition
                    viewModel.deleteExercise(position)
                }
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    val draggedItemIndex = viewHolder.absoluteAdapterPosition
                    val targetIndex: Int = target.absoluteAdapterPosition
                    viewModel.swapPosition(draggedItemIndex, targetIndex)
                    return false
                }
            }
        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(binding.workoutRecyclerView)
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
                findNavController().navigate(NavigationDirections.navigateToHomeFragment())
            }
        }
        viewModel.navigateToHomeFragment.observe(viewLifecycleOwner) {
            if (it == true) {
                dialog.dismiss()
                findNavController().navigate(NavigationDirections.navigateToHomeFragment())
            }
        }
        dialogBinding.rejectText.setOnClickListener {
            if (!binding.workoutTitleText.text.isNullOrBlank()) {
                viewModel.finishWorkoutWithoutSaveTemplate(binding.workoutTitleText.text.toString())
            }
        }
        dialogBinding.root.setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun showCancelDialog() {
        val dialog = Dialog(this.requireContext())
        val dialogBinding: DialogCancelWorkoutBinding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.dialog_cancel_workout,
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
            findNavController().navigate(NavigationDirections.navigateToHomeFragment())
            viewModel.cancelWorkout()
            dialog.dismiss()
        }
        dialogBinding.root.setOnClickListener {
            dialog.dismiss()
        }
    }
}