package com.example.stack.home.template

import android.icu.text.Transliterator.Position
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stack.data.StackRepository
import com.example.stack.data.dataclass.Template
import com.example.stack.data.dataclass.TemplateExerciseRecord
import com.example.stack.login.UserManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class TemplateViewModel @Inject constructor(private val stackRepository: StackRepository) :
    ViewModel() {

    val templateList = MutableLiveData<List<Template>?>()

    val templateExerciseList = MutableLiveData<List<TemplateExerciseRecord>>()

    fun searchTemplateByUserId() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                var resultList =
                    UserManager.user?.id?.let { stackRepository.searchTemplatesByUserId(it) }
                Log.i("template", "$resultList")

                templateList.postValue(resultList)
            }
        }
    }
    fun searchAllTemplateExerciseByTemplateId(templateId: String){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                val exerciserList =
                    stackRepository.getTemplateExerciseRecordListByTemplateId(templateId)

                templateExerciseList.postValue(exerciserList)
            }
        }
    }
    fun deleteTemplate(template: Template, templatePosition: Int){
        val updatedList = mutableListOf<Template>()
        templateList.value?.let{
            updatedList.addAll(it)
            updatedList.removeAt(templatePosition)
            templateList.value = updatedList
        }
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                stackRepository.deleteTemplateByTemplateId(templateId = template.templateId)
            }
        }
    }
}