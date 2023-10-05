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
    val title = MutableLiveData<String>()
    fun getInstruction(youtubeId: String) {

        var instructionString: String = ""

        val promptPrefix =
            "This is the transcript of a exercise tutorial youtube video, summarize the transcript and list each step so that reader can " +
                    "follow the instructions to learn the exercise. Each step should end with line break. The following is the video transcript:"

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
                        val response = stackRepository.getInstruction(ChatGptRequest(prompt = (promptPrefix + transcript)))
                        if(response != null){
                            instructionString = response.choices[0].text
                        }

                        exerciseYoutube.instruction = instructionString
                    }

                    stackRepository.updateYoutubeData(exerciseYoutube)
                    Log.i("chatgpt", "get instruction from chatgpt ${exerciseYoutube.instruction}")

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


