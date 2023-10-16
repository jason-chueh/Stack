package com.example.stack.home.workout

import android.util.Log
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
    @Assisted
    private val test: String
) :
    ViewModel() {
    val dataList = MutableLiveData<List<ExerciseRecordWithCheck>>()

    private var startTime = System.currentTimeMillis()

    val exerciseList = MutableLiveData<List<Exercise>>()

    val scrollToPosition = MutableLiveData<Int>()

    val scrollToInnerPosition = MutableLiveData<IntPair>()

    val smoothScrollTarget = MutableLiveData<IntPair>()

    val filteredExerciseList = MutableLiveData<List<ExerciseWithCheck>>()

    val notifyItemChangePosition = MutableLiveData<Int>()

    companion object {
        fun provideWorkoutViewModelFactory(
            factory: Factory,
            test: String
        ): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return factory.create(test) as T
                }
            }
        }
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

    fun calculateScroll(exercisePosition: Int, setPosition: Int){
        dataList.value?.let{
            // if it is the last set being checked, the screen should scroll to position of next exercise
            if(it[exercisePosition].repsAndWeights.size == setPosition + 1) {
                //if the exercise is not the last one
                if(exercisePosition != it.size - 1){
                    scrollToPosition.value = exercisePosition + 1
                }
            }
            // else, scroll by the height of a viewHolder
            else{
                smoothScrollTarget.value = IntPair(exercisePosition,setPosition + 1)
            }
        }

    }

    fun setDataListFromBundle(templateExerciseList: List<TemplateExerciseRecord>) {
        if (UserManager.user?.id != null) {
            startTime = System.currentTimeMillis()
            dataList.value = templateExerciseList.map {
                it.toTemplateExerciseWithCheck(
                    UserManager.user!!.id,
                    startTime
                )
            }
        }
    }

    fun cancelWorkout() {
        UserManager.isTraining = false
    }


    fun finishWorkoutWithSaveTemplate(workoutName: String) {
        UserManager.isTraining = false
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                if (UserManager.user?.id != null) {

                    val workoutToUpload = Workout(
                        userId = UserManager.user!!.id,
                        workoutName = workoutName,
                        startTime = startTime,
                        endTime = Calendar.getInstance().timeInMillis
                    )
                    stackRepository.upsertWorkout(
                        workoutToUpload
                    )
                    stackRepository.upsertTemplate(workoutToUpload.WorkoutToTemplate(workoutToUpload.startTime.toString()))
                    val filteredExerciseRecords = dataList.value?.map { exerciseRecord ->
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
                }
            }
        }
    }

    fun finishWorkoutWithoutSaveTemplate(workoutName: String) {
        UserManager.isTraining = false
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                if (UserManager.user?.id != null) {

                    val workoutToUpload = Workout(
                        userId = UserManager.user!!.id,
                        workoutName = workoutName,
                        startTime = startTime,
                        endTime = Calendar.getInstance().timeInMillis
                    )
                    stackRepository.upsertWorkout(
                        workoutToUpload
                    )
                    val filteredExerciseRecords = dataList.value?.map { exerciseRecord ->
                        exerciseRecord.copy(
                            repsAndWeights = exerciseRecord.repsAndWeights.filter { it.check }
                                .toMutableList()
                        )
                    }?.filter { it.repsAndWeights.isNotEmpty() }

                    Log.i("finishWorkout", "$filteredExerciseRecords")
                    val exerciseRecordsListToUpload =
                        filteredExerciseRecords?.map { it.toExerciseRecord() }
                    exerciseRecordsListToUpload?.let { stackRepository.upsertExerciseRecordList(it) }


                }
            }
        }
    }


    fun getAllExerciseFromDb() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val resultList = stackRepository.getAllExercise()
                    exerciseList.postValue(resultList)
                    filteredExerciseList.postValue(resultList.map { it.toExerciseWithCheck() })
                } catch (e: Exception) {
                    Log.i("workout", "$e")
                }
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


    fun addExerciseRecord(exerciseId: String, exerciseName: String) {
        dataList.value?.let { oldList ->
            val newList = mutableListOf<ExerciseRecordWithCheck>()
            newList.apply {
                addAll(oldList)
                add(
                    ExerciseRecordWithCheck(
                        UserManager.user!!.id,
                        startTime,
                        exerciseId,
                        exerciseName,
                        false,
                        mutableListOf()
                    )
                )
            }
            dataList.value = newList

            scrollToPosition.value = dataList.value?.size?.minus(1)
        }
    }

    fun deleteExercise(exercisePosition: Int){
        Log.i("swipe","delete position: $exercisePosition")

        val updatedList = mutableListOf<ExerciseRecordWithCheck>()
        dataList.value?.let{
            updatedList.addAll(it)
            updatedList.removeAt(exercisePosition)
            dataList.value = updatedList
        }
    }

    fun swapPosition(draggedItemIndex: Int, targetIndex: Int){
        val updatedList = mutableListOf<ExerciseRecordWithCheck>()
        dataList.value?.let{
            updatedList.addAll(it)
            updatedList.swap(draggedItemIndex, targetIndex)
            dataList.value = updatedList
        }
    }

    val deleteExerciseRecord: (exercisePosition: Int, setPosition: Int) -> Unit =
        { exercisePosition, setPosition ->
            val updatedList = mutableListOf<ExerciseRecordWithCheck>()
            dataList.value?.let { updatedList.addAll(it) }
            val newList = mutableListOf<RepsAndWeightsWithCheck>()
            newList.addAll(updatedList[exercisePosition].repsAndWeights)
            newList.removeAt(setPosition)
            updatedList[exercisePosition] = updatedList[exercisePosition].copy(repsAndWeights = newList)
            dataList.value = updatedList
        }


    val expandExercise: (exercisePosition: Int) -> Unit = { exercisePosition ->
        val updatedList = mutableListOf<ExerciseRecordWithCheck>()
        dataList.value?.let { updatedList.addAll(it) }
        updatedList[exercisePosition] =
            updatedList[exercisePosition].copy(expand = !updatedList[exercisePosition].expand)
        dataList.value = updatedList
//        scrollToPosition.value = exercisePosition
    }

    val addSet: (exercisePosition: Int) -> Unit = { exercisePosition ->
        val updatedList = mutableListOf<ExerciseRecordWithCheck>()

        dataList.value?.let { updatedList.addAll(it) }
        val newList = mutableListOf<RepsAndWeightsWithCheck>()
        newList.addAll(updatedList[exercisePosition].repsAndWeights)
        newList.add(RepsAndWeightsWithCheck(0, 0))
        updatedList[exercisePosition] = updatedList[exercisePosition].copy(repsAndWeights = newList)
//        Log.i("workout",${})
//        scrollToInnerPosition.postValue(IntPair(exercisePosition, updatedList[exercisePosition].repsAndWeights.size-1))
        dataList.value = updatedList
        smoothScrollTarget.value = IntPair(exercisePosition, 0)
    }

    fun updateSetToTrue(
        exercisePosition: Int,
        setPosition: Int,
        repsAndWeights: RepsAndWeightsWithCheck
    ) {
        val updatedList = mutableListOf<ExerciseRecordWithCheck>()
        dataList.value?.let { updatedList.addAll(it) }
        updatedList[exercisePosition].repsAndWeights[setPosition] = repsAndWeights.copy(
            reps = repsAndWeights.reps,
            weight = repsAndWeights.weight,
            check = true
        )
        dataList.value = updatedList
//        notifyItemChangePosition.value = exercisePosition
//        scrollToPosition.value = exercisePosition
    }

    val updateSetToFalse: (exercisePosition: Int, setPosition: Int, repsAndWeights: RepsAndWeightsWithCheck) -> Unit =
        { exercisePosition, setPosition, repsAndWeights ->
            Log.i("cancel", "$exercisePosition ,$setPosition")
            val updatedList = mutableListOf<ExerciseRecordWithCheck>()
            dataList.value?.let { updatedList.addAll(it) }
            updatedList[exercisePosition].repsAndWeights[setPosition] =
                updatedList[exercisePosition].repsAndWeights[setPosition].copy(
                    check = false
                )
            dataList.value = updatedList
        }
}


data class IntPair(
    var first: Int,
    var second: Int
)
