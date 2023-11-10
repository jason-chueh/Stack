package com.example.stack.home.template

import android.util.Log
import androidx.lifecycle.LiveData
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
class TemplateViewModel @Inject constructor(
    private val stackRepository: StackRepository,
    private val userManager: UserManager
) :
    ViewModel() {

    private val _templateList = MutableLiveData<List<Template>?>()
    val templateList: LiveData<List<Template>?>
        get() = _templateList

    private val _templateExerciseList = MutableLiveData<List<TemplateExerciseRecord>>()

    val templateExerciseList: LiveData<List<TemplateExerciseRecord>>
        get() = _templateExerciseList

    fun searchTemplateByUserId() {
        viewModelScope.launch(Dispatchers.IO) {
            val resultList =
                userManager.user?.id?.let { stackRepository.searchTemplatesByUserId(it) }
            _templateList.postValue(resultList)
        }
    }

    fun searchAllTemplateExerciseByTemplateId(templateId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val exerciserList =
                stackRepository.getTemplateExerciseRecordListByTemplateId(templateId)
            _templateExerciseList.postValue(exerciserList)
        }
    }

    fun deleteTemplate(template: Template, templatePosition: Int) {
        val updatedList = mutableListOf<Template>()
        _templateList.value?.let {
            updatedList.addAll(it)
            updatedList.removeAt(templatePosition)
            _templateList.value = updatedList
        }
        viewModelScope.launch(Dispatchers.IO) {
            stackRepository.deleteTemplateByTemplateId(templateId = template.templateId)
        }
    }
}