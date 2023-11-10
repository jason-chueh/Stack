package com.example.stack.home.instruction

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stack.data.StackRepository
import com.example.stack.data.dataclass.ChatGptRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InstructionViewModel @Inject constructor(private val stackRepository: StackRepository) :
    ViewModel() {

    private val _instruction = MutableLiveData<String>()
    val instruction: LiveData<String>
        get() = _instruction

    val title = MutableLiveData<String>()
    fun getInstruction(youtubeId: String) {

        val promptPrefix =
            "This is the transcript of a youtube video, summarize it and turn it into bullet points instruction, the instruction should follow the below format:\n" +
                    "<div>Step 1:</div>\n" +
                    "Ensure the bar tracks over your midfoot for balance.\n" +
                    "<hr>\n" +
                    "<div>Step 2:</div>\n" +
                    "Address ankle mobility if your chest collapses on descent. Stretch your calf by driving the knee forward over the toes.\n" +
                    "<hr>\n" +
                    "<div>Step 3:</div>\n" +
                    "Address ankle mobility if your chest collapses on descent. Stretch your calf by driving the knee forward over the toes.\n" +
                    "<br>\n" +
                    "\n" +
                    "Noted that the every bullet point should end with <hr>, except the last bullet point should end with <br>, and don't add anything before or after the instructions content, just give me only the instruction, the response only can start with <div> and end with <br>\n" +
                    "The below is the transcript:\n"

        //transcript == null means that the video haven't been searched before, transcript == "" means the transcript does not exist
        viewModelScope.launch(Dispatchers.IO) {
            var instructionString: String = ""

            val exerciseYoutube = stackRepository.searchYoutubeByYoutubeId(youtubeId)
            when (exerciseYoutube.transcript) {
                null -> {
                    val transcript = stackRepository.getTranscript(youtubeId)
                    //call chat gpt to generate instruction, and update transcript & instruction to database

                    exerciseYoutube.transcript = transcript

                    if (transcript == "") {
                        instructionString = "The transcript is not available in this tutorial."
                    } else {
                        val response =
                            stackRepository.getInstruction(ChatGptRequest(prompt = (promptPrefix + transcript)))
                        if (response != null) {
                            instructionString = response.choices[0].text
                        }
                        else{
                            instructionString = "The transcript fail to be converted into instructions by chatgpt"
                        }

                        exerciseYoutube.instruction = instructionString
                    }

                    stackRepository.updateYoutubeData(exerciseYoutube)

                }

                "" -> {

                    instructionString = "The transcript is not available in this tutorial."
                }

                else -> {

                    instructionString = exerciseYoutube.instruction!!
                }
            }
            _instruction.postValue(instructionString)
        }
    }

}


