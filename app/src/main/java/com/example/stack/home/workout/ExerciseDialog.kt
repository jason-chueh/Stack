package com.example.stack.home.workout

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.stack.R
import com.example.stack.data.dataclass.Exercise
import com.example.stack.databinding.DialogExerciseListBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ExerciseDialog : AppCompatDialogFragment() {
    lateinit var binding: DialogExerciseListBinding

    @Inject
    lateinit var factory: WorkoutViewModel.Factory
    private val viewModel: WorkoutViewModel by activityViewModels {
        WorkoutViewModel.provideWorkoutViewModelFactory(factory, "test")
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.LoginDialog)
        Log.i("workout", "dialog: $viewModel")

        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogExerciseListBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

        var name: String = ""
        var id: String = ""

        val adapter = ExerciseAdapter(fun(exerciseId: String, exerciseName: String) {
            name = exerciseName
            id = exerciseId
        })
        binding.next.setOnClickListener {
            if (name != "" && id != "") {
                viewModel.addExerciseRecord(id, name)
                dismiss()
            }
        }

        binding.cancelAction.setOnClickListener {
            var tempList = mutableListOf<Exercise>()
            viewModel.exerciseList.value?.let { it1 -> tempList.addAll(it1) }
            viewModel.filteredExerciseList.value = tempList
            dismiss()
        }

        binding.filterIcon.setOnClickListener {
            val dialog = ExerciseFilterDialog()
            dialog.show(childFragmentManager, "EXERCISE_FILTER_TAG")
        }

        binding.exerciseRecyclerView.adapter = adapter


        viewModel.filteredExerciseList.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }


        return binding.root
    }

    interface ExerciseDialogListener {
        fun onExerciseSelected(position: Int)
    }

    // let any intense can set the listener itself through setCategoryDialogListener()
    private var listener: ExerciseDialogListener? = null
    fun setExerciseDialogListener(listener: ExerciseDialogListener) {
        this.listener = listener
    }
}