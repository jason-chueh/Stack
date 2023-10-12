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


