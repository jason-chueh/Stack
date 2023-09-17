package com.example.stack.home

import android.os.Parcelable
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.stack.data.StackRepository
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.android.parcel.Parcelize
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val stackRepository: StackRepository) : ViewModel() {

    fun test(){
        stackRepository.test2()
    }
}

@Parcelize
data class Exercise(
    val name:String = "",
    val content: String = ""
): Parcelable