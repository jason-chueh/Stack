package com.example.stack.home.instruction

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stack.data.StackRepository
import com.example.stack.data.dataclass.ChatGptRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class InstructionViewModel @Inject constructor(private val stackRepository: StackRepository) :
    ViewModel() {

    val instruction = MutableLiveData<String>()
    fun getInstruction(youtubeId: String) {

        var instructionString: String = ""

        val promptPrefix =
            "This is the transcript of a youtube video, summarize the transcript and turn it into instructions"

        //trancript == null means that the video haven't been searched before, transcript == "" means the transcript does not exist
        viewModelScope.launch {
            withContext(Dispatchers.IO) {

                var exerciseYoutube = stackRepository.searchYoutubeByYoutubeId(youtubeId)
                if (exerciseYoutube.transcript == null) {
                    val transcript = stackRepository.getTranscript(youtubeId)
                    //call chat gpt to generate instruction, and update transcript & instruction to database

                    exerciseYoutube.transcript = transcript

                    if (transcript == "") {
                        instructionString = "The transcript is not available in this tutorial."
                    } else {
                        instructionString =
                            stackRepository.getInstruction(ChatGptRequest(prompt = (promptPrefix + transcript))).choices[0].text
                        exerciseYoutube.instruction = instructionString
                    }

                    stackRepository.updateYoutubeData(exerciseYoutube)
                    Log.i("chatgpt", "${exerciseYoutube.instruction}")

                } else if (exerciseYoutube.transcript == "") {
                    instructionString = "The transcript is not available in this tutorial."
                } else {
                    Log.i("chatgpt", "${exerciseYoutube.transcript}")
                    instructionString = exerciseYoutube.instruction!!
                }
            }
            instruction.value = instructionString
        }
    }
}

