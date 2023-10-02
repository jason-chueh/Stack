package com.example.stack.user

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stack.data.StackRepository
import com.example.stack.data.dataclass.ExerciseRecord
import com.example.stack.data.dataclass.Workout
import com.example.stack.login.UserManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(private val stackRepository: StackRepository) :
    ViewModel() {
    val userExerciseRecords = MutableLiveData<List<ExerciseRecord>?>()
    val userWorkoutRecords = MutableLiveData<List<Workout>?>()
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