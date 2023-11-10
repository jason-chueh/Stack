package com.example.stack.chatroom

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stack.data.StackRepository
import com.example.stack.data.dataclass.Chat
import com.example.stack.data.dataclass.Chatroom
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val stackRepository: StackRepository,
) :
    ViewModel() {

    val db = Firebase.firestore
    private val ref = db.collection("chat")
    private var registration: ListenerRegistration? = null
    private val _chatList = MutableLiveData<List<Chat>>()
    val chatList: LiveData<List<Chat>>
        get() = _chatList
    private var timer = 0

    fun sendMessage(chat: Chat) {
        viewModelScope.launch(Dispatchers.IO){
            stackRepository.sendChatMessageToFireStore(chat)
        }
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
                        timer ++
                        result.add(doc.toObject<Chat>())
                    }
                    Log.i("chat","$result")
                    _chatList.postValue(result.toList())
                }catch(e: Exception){
                    Log.i("chat","$e")
                }
                Log.i("chat","timer: $timer")
            }
        }
    }

    fun removeListener(){
        Log.i("listener","ListenerRemoved!")
        registration?.remove()
    }
    fun updateChatroom(chatroom: Chatroom){
        viewModelScope.launch(Dispatchers.IO){
            stackRepository.updateChatroom(chatroom)
        }
    }
}