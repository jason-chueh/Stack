package com.example.stack.chatroom

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.stack.data.StackRepository
import com.example.stack.data.dataclass.Chat
import com.example.stack.data.dataclass.Chatroom
import com.example.stack.home.workout.WorkoutViewModel
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val stackRepository: StackRepository,
) :
    ViewModel() {

    val db = Firebase.firestore
    val ref = db.collection("chat")
    var registration: ListenerRegistration? = null
    var chatList = MutableLiveData<List<Chat>>()



    fun sendMessage(chat: Chat) {
        stackRepository.sendChatMessageToFireStore(chat)
    }

    fun addListener(chatroomId: String){
        registration = ref.whereEqualTo("chatroomId",chatroomId).orderBy("sendTime").addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.i("chat", "Listen failed.", e)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                try{
                    val result = mutableListOf<Chat>()

                    for (doc in snapshot) {
                        result.add(doc.toObject<Chat>())
                    }
                    Log.i("chat","$result")
                    chatList.postValue(result.toList())
                }catch(e: Exception){
                    Log.i("chat","$e")
                }
            }
        }
    }

    fun removeListener(){
        registration?.remove()
    }
    fun updateChatroom(chatroom: Chatroom){
        stackRepository.updateChatroom(chatroom)
    }
}