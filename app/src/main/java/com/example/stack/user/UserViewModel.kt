package com.example.stack.user

import android.util.Log
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stack.data.StackRepository
import com.example.stack.data.dataclass.ExerciseRecord
import com.example.stack.data.dataclass.Workout
import com.example.stack.login.UserManager
import com.github.mikephil.charting.data.Entry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(private val stackRepository: StackRepository) :
    ViewModel() {
    val userExerciseRecords = MutableLiveData<List<ExerciseRecord>?>()
    val userWorkoutRecords = MutableLiveData<List<Workout>?>()
    val weightSum = MutableLiveData<Int?>()

    fun getExerciseEntry(): List<Entry>?{
        if(userExerciseRecords.value != null){
            val exerciseRecords: List<ExerciseRecord> = userExerciseRecords.value!!
            val groupedRecords: Map<Long, List<ExerciseRecord>> = exerciseRecords.groupBy { it.startTime }
            val summedRecords: List<Pair<Long, Int>> = groupedRecords.map { (startTime, records) ->
                val sum = records.flatMap { it.repsAndWeights }
                    .sumBy { repsAndWeight -> repsAndWeight.reps * repsAndWeight.weight }
                Pair(startTime, sum)
            }
            val resultEntries: List<Entry> = summedRecords.map { (startTime, sum) ->
                Entry(((Calendar.getInstance().timeInMillis - startTime)/3600000).toFloat(), (sum/1000).toFloat())
            }
            Log.i("mpAndroid","$resultEntries")
            return resultEntries
        }
        return null
    }

    fun sumUpExerciseRecords(){
        val sums: List<Int>? = userExerciseRecords.value?.map { exerciseRecord ->
            exerciseRecord.repsAndWeights.sumBy { it.reps * it.weight }
        }
        val totalSum: Int? = sums?.sum()

        weightSum.value = totalSum
    }
    fun getUserExerciseData() {

        viewModelScope.launch {
            withContext(Dispatchers.IO){
                val exerciseRecordList = UserManager.user?.id?.let { stackRepository.getAllExercisesByUserId(it) }

                userExerciseRecords.postValue(exerciseRecordList)
            }
        }
    }

    fun getUserWorkoutData(){
        viewModelScope.launch{
            withContext(Dispatchers.IO){
                val workoutList = UserManager.user?.id?.let{ stackRepository.findAllWorkoutById(it)}

                userWorkoutRecords.postValue(workoutList)
            }
        }
    }
}