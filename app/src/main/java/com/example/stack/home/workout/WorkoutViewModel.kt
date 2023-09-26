package com.example.stack.home.workout

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.stack.data.StackRepository
import com.example.stack.data.dataclass.ExerciseRecord
import com.example.stack.data.dataclass.ExerciseRecordWithCheck
import com.example.stack.data.dataclass.RepsAndWeights
import com.example.stack.data.dataclass.RepsAndWeightsWithCheck
import com.example.stack.data.dataclass.toExerciseRecordWithCheck
import com.example.stack.login.UserManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WorkoutViewModel @Inject constructor(private val stackRepository: StackRepository) :
    ViewModel() {
    val dataList = MutableLiveData<List<ExerciseRecordWithCheck>>()
    val startTime = System.currentTimeMillis()

    val mockData1 = ExerciseRecord(
        userId = "user123",
        startTime = System.currentTimeMillis(),
        exerciseId = "exercise1",
        exerciseName = "Push-ups",
        repsAndWeights = mutableListOf(RepsAndWeights(reps = 15, weight = 0),RepsAndWeights(reps = 15, weight = 0))
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

    val list = mutableListOf(mockData1,mockData2,mockData3,mockData4).map{it.toExerciseRecordWithCheck()}.toMutableList()

    init{
        dataList.value = list
    }

    
    fun addExerciseRecord(exerciseId: String, exerciseName: String){
        var addedList = mutableListOf<ExerciseRecordWithCheck>()
        list.add(ExerciseRecordWithCheck(UserManager.user!!.id, startTime ,exerciseId, exerciseName, mutableListOf()))
        var newList = mutableListOf<ExerciseRecordWithCheck>()
        newList.addAll(list)
        dataList.value = newList
    }

    val addSet: (exercisePosition:Int)->Unit = {exercisePosition->
        Log.i("jimmy","$exercisePosition")
        val updatedList = mutableListOf<ExerciseRecordWithCheck>()
        dataList.value?.let {updatedList.addAll(it)}
        var updatedRepsAndWeights = mutableListOf<RepsAndWeightsWithCheck>()
        updatedList[exercisePosition].repsAndWeights.add(RepsAndWeightsWithCheck(0,0))
        updatedRepsAndWeights.addAll(updatedList[exercisePosition].repsAndWeights)
        updatedList[exercisePosition].repsAndWeights = updatedRepsAndWeights
        dataList.value = updatedList
    }
    val updateSetToTrue : (exercisePosition:Int, setPosition: Int, repsAndWeights: RepsAndWeightsWithCheck)->Unit = { exercisePosition, setPosition, repsAndWeights ->
        Log.i("jimmy","$exercisePosition")
        Log.i("jimmy","$setPosition")
        Log.i("jimmy", "$repsAndWeights")
        val updatedList = mutableListOf<ExerciseRecordWithCheck>()
        dataList.value?.let { updatedList.addAll(it) }
        updatedList[exercisePosition].repsAndWeights[setPosition] = repsAndWeights.copy(check = true)
        dataList.value = updatedList
        Log.i("jimmy","$updatedList")
    }

    val updateSetToFalse : (exercisePosition:Int, setPosition: Int, repsAndWeights: RepsAndWeightsWithCheck)->Unit = { exercisePosition, setPosition, repsAndWeights ->
        Log.i("jimmy","$exercisePosition")
        Log.i("jimmy","$setPosition")
        Log.i("jimmy", "$repsAndWeights")
        val updatedList = mutableListOf<ExerciseRecordWithCheck>()
        dataList.value?.let { updatedList.addAll(it) }
        updatedList[exercisePosition].repsAndWeights[setPosition] = repsAndWeights.copy(check = false)
        dataList.value = updatedList
        Log.i("jimmy","$updatedList")
    }
}