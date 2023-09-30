package com.example.stack.home.workout

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.stack.data.StackRepository
import com.example.stack.data.dataclass.Exercise
import com.example.stack.data.dataclass.ExerciseRecord
import com.example.stack.data.dataclass.ExerciseRecordWithCheck
import com.example.stack.data.dataclass.RepsAndWeights
import com.example.stack.data.dataclass.RepsAndWeightsWithCheck
import com.example.stack.data.dataclass.toExerciseRecordWithCheck
import com.example.stack.login.UserManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


class WorkoutViewModel @AssistedInject constructor(
    private val stackRepository: StackRepository,
    @Assisted
    private val test: String
) :
    ViewModel() {
    val dataList = MutableLiveData<List<ExerciseRecordWithCheck>>(list)
    val startTime = System.currentTimeMillis()

    val exerciseList = MutableLiveData<List<Exercise>>()

    val filteredExerciseList = MutableLiveData<List<Exercise>>()

    @AssistedFactory
    interface Factory {
        fun create(test: String): WorkoutViewModel
    }

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

    init {
        dataList.value = list
    }


    fun getAllExerciseFromDb() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val resultList = stackRepository.getAllExercise()
                    exerciseList.postValue(resultList)
                    filteredExerciseList.postValue(resultList)
                } catch (e: Exception) {
                    Log.i("workout", "$e")
                }
            }
        }
    }


    fun addExerciseRecord(exerciseId:String, exerciseName: String) {

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
                            mutableListOf()
                        )
                    )
            }
            dataList.value = newList
        }
    }

    val addSet: (exercisePosition: Int) -> Unit = { exercisePosition ->
        val updatedList = mutableListOf<ExerciseRecordWithCheck>()

        dataList.value?.let { updatedList.addAll(it) }

        updatedList[exercisePosition].repsAndWeights.add(RepsAndWeightsWithCheck(0, 0))

        dataList.value = updatedList
    }

    val updateSetToTrue: (exercisePosition: Int, setPosition: Int, repsAndWeights: RepsAndWeightsWithCheck) -> Unit =
        { exercisePosition, setPosition, repsAndWeights ->

            val updatedList = mutableListOf<ExerciseRecordWithCheck>()
            dataList.value?.let { updatedList.addAll(it) }
            updatedList[exercisePosition].repsAndWeights[setPosition] = repsAndWeights.copy(
                reps = repsAndWeights.reps,
                weight = repsAndWeights.weight,
                check = true
            )
            dataList.value = updatedList
        }

    val updateSetToFalse: (exercisePosition: Int, setPosition: Int, repsAndWeights: RepsAndWeightsWithCheck) -> Unit =
        { exercisePosition, setPosition, repsAndWeights ->
            Log.i("cancel", "$exercisePosition ,$setPosition")
            val updatedList = mutableListOf<ExerciseRecordWithCheck>()
            dataList.value?.let { updatedList.addAll(it) }
            updatedList[exercisePosition].repsAndWeights[setPosition] =
                updatedList[exercisePosition].repsAndWeights[setPosition].copy(
                    reps = 0,
                    weight = 0,
                    check = false
                )
            dataList.value = updatedList

        }

    fun finishWorkout() {

    }


}

val mockData1 = ExerciseRecord(
    userId = "user123",
    startTime = System.currentTimeMillis(),
    exerciseId = "0026",
    exerciseName = "barbell bench squat",
    repsAndWeights = mutableListOf(
        RepsAndWeights(reps = 15, weight = 0),
        RepsAndWeights(reps = 15, weight = 0)
    )
)

val mockData2 = ExerciseRecord(
    userId = "user123",
    startTime = System.currentTimeMillis(),
    exerciseId = "exercise2",
    exerciseName = "Squats",
    repsAndWeights = mutableListOf(RepsAndWeights(reps = 20, weight = 30))
)

val mockData3 = ExerciseRecord(
    userId = "user123",
    startTime = System.currentTimeMillis(),
    exerciseId = "exercise3",
    exerciseName = "Bench Press",
    repsAndWeights = mutableListOf(RepsAndWeights(reps = 10, weight = 50))
)

val mockData4 = ExerciseRecord(
    userId = "user123",
    startTime = System.currentTimeMillis(),
    exerciseId = "exercise4",
    exerciseName = "Pull-ups",
    repsAndWeights = mutableListOf(RepsAndWeights(reps = 8, weight = 0))
)

val list = mutableListOf(
    mockData1,
    mockData2,
    mockData3,
    mockData4
).map { it.toExerciseRecordWithCheck() }.toMutableList()