package com.example.stack.home.workout

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.stack.data.StackRepository
import com.example.stack.data.dataclass.Exercise
import com.example.stack.data.dataclass.ExerciseRecordWithCheck
import com.example.stack.data.dataclass.ExerciseWithCheck
import com.example.stack.data.dataclass.RepsAndWeightsWithCheck
import com.example.stack.data.dataclass.TemplateExerciseRecord
import com.example.stack.data.dataclass.Workout
import com.example.stack.data.dataclass.WorkoutToTemplate
import com.example.stack.data.dataclass.toExerciseRecord
import com.example.stack.data.dataclass.toExerciseWithCheck
import com.example.stack.data.dataclass.toTemplateExerciseRecord
import com.example.stack.data.dataclass.toTemplateExerciseWithCheck
import com.example.stack.login.UserManager
import com.example.stack.util.swap
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar


class WorkoutViewModel @AssistedInject constructor(
    private val stackRepository: StackRepository,
    private val userManager: UserManager,

    @Assisted
    private val userId: String
) :
    ViewModel() {
    private val _dataList = MutableLiveData<List<ExerciseRecordWithCheck>>()
    val dataList: LiveData<List<ExerciseRecordWithCheck>> = _dataList

    private var startTime = System.currentTimeMillis()

    val exerciseList = MutableLiveData<List<Exercise>>()

    val scrollToPosition = MutableLiveData<Int>()

    val smoothScrollTarget = MutableLiveData<PositionPair>()

    val filteredExerciseList = MutableLiveData<List<ExerciseWithCheck>>()

    val notifyItemChangePosition = MutableLiveData<Int>()

    val navigateToHomeFragment = MutableLiveData<Boolean>(false)

    companion object {
        fun provideWorkoutViewModelFactory(
            factory: Factory,
            userId: String
        ): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return factory.create(userId) as T
                }
            }
        }
    }

    init {
        userManager.updateIsTraining(true)
    }

    val updateExerciseListCheck: (Int) -> Unit = { position ->
        val updateList = mutableListOf<ExerciseWithCheck>()
        filteredExerciseList.value?.let { updateList.addAll(it) }
        updateList[position] = updateList[position].copy(check = !updateList[position].check)
        filteredExerciseList.value = updateList
    }

    @AssistedFactory
    interface Factory {
        fun create(test: String): WorkoutViewModel
    }

    fun calculateScroll(exercisePosition: Int, setPosition: Int) {
        _dataList.value?.let {
            // if it is the last set being checked, the screen should scroll to position of next exercise
            if (it[exercisePosition].repsAndWeights.size == setPosition + 1) {
                //if the exercise is not the last one
                if (exercisePosition != it.size - 1) {
                    scrollToPosition.value = exercisePosition + 1
                }
            }
            // else, scroll by the height of a viewHolder
            else {
                smoothScrollTarget.value = PositionPair(exercisePosition, setPosition + 1)
            }
        }
    }

    fun setDataListFromBundle(templateExerciseList: List<TemplateExerciseRecord>) {
        if (userManager.user?.id != null) {
            startTime = System.currentTimeMillis()
            _dataList.value = templateExerciseList.map {
                it.toTemplateExerciseWithCheck(
                    userManager.user!!.id,
                    startTime
                )
            }
        }
    }

    fun cancelWorkout() {
        userManager.updateIsTraining(false)
    }

//TODO rewrite the logic
    fun finishWorkoutWithSaveTemplate(workoutName: String) {
        userManager.updateIsTraining(false)
        viewModelScope.launch(Dispatchers.IO) {

            if (userManager.user?.id != null) {

                val workoutToUpload = Workout(
                    userId = userManager.user!!.id,
                    workoutName = workoutName,
                    startTime = startTime,
                    endTime = Calendar.getInstance().timeInMillis
                )

                stackRepository.upsertWorkout(
                    workoutToUpload
                )

                stackRepository.upsertTemplate(workoutToUpload.WorkoutToTemplate(workoutToUpload.startTime.toString()))
                val filteredExerciseRecords = _dataList.value?.map { exerciseRecord ->
                    exerciseRecord.copy(
                        repsAndWeights = exerciseRecord.repsAndWeights.filter { it.check }
                            .toMutableList()
                    )
                }?.filter { it.repsAndWeights.isNotEmpty() }

                Log.i("finishWorkout", "$filteredExerciseRecords")
                val exerciseRecordsListToUpload =
                    filteredExerciseRecords?.map { it.toExerciseRecord() }
                exerciseRecordsListToUpload?.let { stackRepository.upsertExerciseRecordList(it) }
                exerciseRecordsListToUpload?.map { exerciseRecord ->
                    exerciseRecord.toTemplateExerciseRecord(
                        exerciseRecord.startTime.toString()
                    )
                }
                    ?.let { stackRepository.upsertTemplateExerciseRecord(it) }
                navigateToHomeFragment.postValue(true)
            }
        }
    }

    fun finishWorkoutWithoutSaveTemplate(workoutName: String) { //TODO fix logic
        userManager.updateIsTraining(false)
        viewModelScope.launch(Dispatchers.IO) {

                if (userManager.user?.id != null) {

                    val workoutToUpload = Workout(
                        userId = userManager.user!!.id,
                        workoutName = workoutName,
                        startTime = startTime,
                        endTime = Calendar.getInstance().timeInMillis
                    )
                    stackRepository.upsertWorkout(
                        workoutToUpload
                    )
                    val filteredExerciseRecords = _dataList.value?.map { exerciseRecord ->
                        exerciseRecord.copy(
                            repsAndWeights = exerciseRecord.repsAndWeights.filter { it.check }
                                .toMutableList()
                        )
                    }?.filter { it.repsAndWeights.isNotEmpty() }

                    Log.i("finishWorkout", "$filteredExerciseRecords")
                    val exerciseRecordsListToUpload =
                        filteredExerciseRecords?.map { it.toExerciseRecord() }
                    exerciseRecordsListToUpload?.let { stackRepository.upsertExerciseRecordList(it) }

                    navigateToHomeFragment.postValue(true)
                }

        }
    }

    fun getAllExerciseFromDb() {
        viewModelScope.launch(Dispatchers.IO) {
                try {
                    val resultList = stackRepository.getAllExercise()
                    exerciseList.postValue(resultList)
                    filteredExerciseList.postValue(resultList.map { it.toExerciseWithCheck() })
                } catch (e: Exception) {
                    Log.i("workout", "$e")
                }
            }

    }

    fun addAllExercise() {
        filteredExerciseList.value?.let { filterListValue ->
            val exerciseToAddList = filterListValue.filter { it.check }
            for (i in exerciseToAddList) {
                addExerciseRecord(i.id, i.name)
            }
        }
    }


    private fun addExerciseRecord(exerciseId: String, exerciseName: String) {
        _dataList.value?.let { oldList ->
            val newList = mutableListOf<ExerciseRecordWithCheck>()
            newList.apply {
                addAll(oldList)
                add(
                    ExerciseRecordWithCheck(
                        userManager.user!!.id,
                        startTime,
                        exerciseId,
                        exerciseName,
                        false,
                        mutableListOf()
                    )
                )
            }
            _dataList.value = newList
            scrollToPosition.value = _dataList.value?.size?.minus(1)
        }
    }

    fun deleteExercise(exercisePosition: Int) {
        Log.i("swipe", "delete position: $exercisePosition")
        val updatedList = mutableListOf<ExerciseRecordWithCheck>()
        _dataList.value?.let {
            updatedList.addAll(it)
            updatedList.removeAt(exercisePosition)
            _dataList.value = updatedList
        }
    }

    fun swapPosition(draggedItemIndex: Int, targetIndex: Int) {
        val updatedList = mutableListOf<ExerciseRecordWithCheck>()
        _dataList.value?.let {
            updatedList.addAll(it)
            updatedList.swap(draggedItemIndex, targetIndex)
            _dataList.value = updatedList
        }
    }

    val deleteExerciseRecord: (exercisePosition: Int, setPosition: Int) -> Unit =
        { exercisePosition, setPosition ->
            val updatedList = mutableListOf<ExerciseRecordWithCheck>()
            _dataList.value?.let { updatedList.addAll(it) }
            val newList = mutableListOf<RepsAndWeightsWithCheck>()
            newList.addAll(updatedList[exercisePosition].repsAndWeights)
            newList.removeAt(setPosition)
            updatedList[exercisePosition] =
                updatedList[exercisePosition].copy(repsAndWeights = newList)
            _dataList.value = updatedList
        }


    val expandExercise: (exercisePosition: Int) -> Unit = { exercisePosition ->
        val updatedList = mutableListOf<ExerciseRecordWithCheck>()
        _dataList.value?.let { updatedList.addAll(it) }
        updatedList[exercisePosition] =
            updatedList[exercisePosition].copy(expand = !updatedList[exercisePosition].expand)
        _dataList.value = updatedList
    }

    val addSet: (exercisePosition: Int) -> Unit = { exercisePosition ->
        val updatedList = mutableListOf<ExerciseRecordWithCheck>()
        _dataList.value?.let { updatedList.addAll(it) }
        val newList = mutableListOf<RepsAndWeightsWithCheck>()
        newList.addAll(updatedList[exercisePosition].repsAndWeights)
        newList.add(RepsAndWeightsWithCheck(0, 0))
        updatedList[exercisePosition] = updatedList[exercisePosition].copy(repsAndWeights = newList)
        _dataList.value = updatedList
        smoothScrollTarget.value = PositionPair(exercisePosition, 0)
    }

    fun updateSetToTrue(
        exercisePosition: Int,
        setPosition: Int,
        repsAndWeights: RepsAndWeightsWithCheck
    ) {
        val updatedList = mutableListOf<ExerciseRecordWithCheck>()
        _dataList.value?.let { updatedList.addAll(it) }
        updatedList[exercisePosition].repsAndWeights[setPosition] = repsAndWeights.copy(
            reps = repsAndWeights.reps,
            weight = repsAndWeights.weight,
            check = true
        )
        _dataList.value = updatedList
    }

    val updateSetToFalse: (exercisePosition: Int, setPosition: Int, repsAndWeights: RepsAndWeightsWithCheck) -> Unit =
        { exercisePosition, setPosition, repsAndWeight ->
            Log.i("cancel", "$exercisePosition ,$setPosition")
            val updatedList = mutableListOf<ExerciseRecordWithCheck>()
            _dataList.value?.let { updatedList.addAll(it) }
            updatedList[exercisePosition].repsAndWeights[setPosition] =
                repsAndWeight.copy(
                    check = false
                )
            _dataList.value = updatedList
        }
}


data class PositionPair(
    var outerRecyclerViewPosition: Int,
    var innerRecyclerViewPosition: Int
)
