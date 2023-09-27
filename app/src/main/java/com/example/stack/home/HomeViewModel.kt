package com.example.stack.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stack.data.StackRepository
import com.example.stack.data.dataclass.RepsAndWeights
import com.example.stack.data.dataclass.Template
import com.example.stack.data.dataclass.TemplateExerciseRecord
import com.example.stack.login.UserManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val stackRepository: StackRepository) :
    ViewModel() {
    fun fromRepsAndWeightsList(repsAndWeights: List<RepsAndWeights>?): String? {
        return Gson().toJson(repsAndWeights)
    }
    fun toRepsAndWeightsList(repsAndWeightsJson: String?): List<RepsAndWeights>? {
        val type = object : TypeToken<List<RepsAndWeights>>() {}.type
        return Gson().fromJson(repsAndWeightsJson, type)
    }
    fun test() {
        val result = fromRepsAndWeightsList(listOf(RepsAndWeights(3,3),RepsAndWeights(4,4)))
        Log.i("gson","$result")
        val result2 = toRepsAndWeightsList(result)
        Log.i("gson","convert back: $result2")

        viewModelScope.launch {
            val result = stackRepository.test2()
            Log.i("gson","$result")
        }
    }

    fun upsertTemplate(){
        viewModelScope.launch{
            withContext(Dispatchers.IO){
                stackRepository.upsertTemplate(personalTemplate)
            }
        }
    }

    fun searchTemplateByUserId(){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                var resultList = UserManager.user?.id?.let { stackRepository.searchTemplateIdListByUserId(it) }
                Log.i("template","$resultList")
            }
        }
    }

    fun upsertTemplateExerciseRecord(){
        viewModelScope.launch{
            withContext(Dispatchers.IO){
                stackRepository.upsertTemplateExerciseRecord(templateExerciseRecord)
            }
        }
    }

    fun upsertTemplateExerciseRecordList(){
        viewModelScope.launch{
            withContext(Dispatchers.IO){
                stackRepository.upsertTemplateExerciseRecord(listOf(templateExerciseRecord, templateExerciseRecord4, templateExerciseRecord5))
            }
        }
    }
    fun searchAllTemplateAndExerciseByUserId(){
        viewModelScope.launch{
            withContext(Dispatchers.IO){
                val resultList = UserManager.user?.let { stackRepository.searchTemplateIdListByUserId(it.id) }
                if (resultList != null) {
                    for(i in resultList){
                        var exerciserList =  stackRepository.getTemplateExerciseRecordListByTemplateId(i)
                        Log.i("template","$exerciserList")
                    }
                }
            }
        }
    }



    val emptyTemplate = Template(
        templateId = "123",
        userId = "K8O0QzYjHrRkGTyJz1rgXpyaggn2",
        templateName = "Empty template",
    )
    val personalTemplate = Template(
        templateId = "456",
        userId = "K8O0QzYjHrRkGTyJz1rgXpyaggn2",
        templateName = "Full body workout template",
    )
    val templateExerciseRecord = TemplateExerciseRecord(
        templateId = "123",
        exerciseName = "barbell bench front squat",
        exerciseId = "0024",
        repsAndWeights = mutableListOf<RepsAndWeights>()
    )
    val templateExerciseRecord2 = TemplateExerciseRecord(
        templateId = "123",
        exerciseName = "barbell bench press",
        exerciseId = "0025",
        repsAndWeights = mutableListOf<RepsAndWeights>()
    )
    val templateExerciseRecord3 = TemplateExerciseRecord(
        templateId = "123",
        exerciseName = "barbell bench squat",
        exerciseId = "0026",
        repsAndWeights = mutableListOf<RepsAndWeights>()
    )
    val templateExerciseRecord4 = TemplateExerciseRecord(
        templateId = "456",
        exerciseName = "barbell bench squat",
        exerciseId = "0026",
        repsAndWeights = mutableListOf<RepsAndWeights>()
    )
    val templateExerciseRecord5 = TemplateExerciseRecord(
        templateId = "456",
        exerciseName = "barbell biceps curl",
        exerciseId = "2407",
        repsAndWeights = mutableListOf<RepsAndWeights>()
    )
}

