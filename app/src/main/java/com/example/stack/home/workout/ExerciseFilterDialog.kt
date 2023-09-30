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
import com.example.stack.databinding.DialogExerciseFilterBinding
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
@AndroidEntryPoint
class ExerciseFilterDialog: AppCompatDialogFragment() {
    lateinit var binding: DialogExerciseFilterBinding
    @Inject
    lateinit var factory: WorkoutViewModel.Factory
    private val viewModel: WorkoutViewModel by activityViewModels{
        WorkoutViewModel.provideWorkoutViewModelFactory(factory, "test")
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.LoginDialog)
        Log.i("workout","dialog: $viewModel")

        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val selectedMuscleChipsSet = mutableSetOf<String>()
        val selectedEquipmentChipsSet = mutableSetOf<String>()
        binding = DialogExerciseFilterBinding.inflate(inflater, container, false)
        binding.cancelImage.setOnClickListener {
            dismiss()
        }
        binding.applyButton.setOnClickListener {
            val muscleGroupIdList = binding.muscleGroup.checkedChipIds
            muscleGroupIdList.forEach{
                id -> val chip = binding.muscleGroup.findViewById<Chip>(id)
                val chipText = chip.text.toString()
                if(chip.isChecked){
                    selectedMuscleChipsSet.add(chipText)
                }
                else{
                    selectedMuscleChipsSet.remove(chipText)
                }
            }
            val equipmentGroupIdList = binding.equipmentGroup.checkedChipIds
            equipmentGroupIdList.forEach{
                    id -> val chip = binding.equipmentGroup.findViewById<Chip>(id)
                val chipText = chip.text.toString()
                if(chip.isChecked){
                    selectedEquipmentChipsSet.add(chipText)
                }
                else{
                    selectedEquipmentChipsSet.remove(chipText)
                }
            }
            Log.i("filter","${viewModel.exerciseList.value}")
            val filteredList =  viewModel.exerciseList.value?.filter {
                exercise -> (exercise.target in selectedMuscleChipsSet || exercise.secondaryMuscles.any { tag -> selectedMuscleChipsSet.contains(tag)}) && exercise.equipment in selectedEquipmentChipsSet
            }

            viewModel.filteredExerciseList.value = filteredList

            dismiss()
        }

        return binding.root
    }
}