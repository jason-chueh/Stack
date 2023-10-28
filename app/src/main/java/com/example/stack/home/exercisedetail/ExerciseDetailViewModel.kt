package com.example.stack.home.exercisedetail

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stack.data.StackRepository
import com.example.stack.data.dataclass.Exercise
import com.example.stack.data.dataclass.ExerciseYoutube
import com.example.stack.data.dataclass.toExerciseYoutube
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ExerciseDetailViewModel @Inject constructor(private val stackRepository: StackRepository) :
    ViewModel() {

    val videoResultList = MutableLiveData<List<ExerciseYoutube>>()

    var exercise = MutableLiveData<Exercise>()
    fun getVideoList(exerciseId: String, exerciseName: String) {
        viewModelScope.launch {
            var tempList = listOf<ExerciseYoutube>()
            withContext(Dispatchers.IO) {
                val youtubeListFromDb =  stackRepository.searchYoutubeByExercise(exerciseId)
                if (youtubeListFromDb.isNotEmpty()) {
                    Log.i("python", "is not empty: $youtubeListFromDb")
                    // check if Database have any data
                    tempList = youtubeListFromDb

                } else {
                    //if not found any Exercise Youtube Video from Database then call api
                    val result = stackRepository.getYoutubeVideo(exerciseId, exerciseName).map { it.toExerciseYoutube(exerciseId) }
                    tempList = result
                    //insert the outcome to database
                    Log.i("python", "${result}")
                    stackRepository.insertYoutubeList(result)
                }
            }
            videoResultList.value = tempList
        }
    }

    fun getExerciseById(exerciseId: String){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                var result = stackRepository.getExerciseById(exerciseId)
                result?.let{exercise.postValue(it)}
            }
        }
    }

    fun test(exercise: ExerciseYoutube) {

    }
}