package com.example.stack.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stack.data.StackRepository
import com.example.stack.data.dataclass.RepsAndWeights
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch

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
}

