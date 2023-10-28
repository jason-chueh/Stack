package com.example.stack.chatroom

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stack.data.StackRepository
import com.example.stack.data.dataclass.Chatroom
import com.example.stack.login.UserManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class ChatroomViewModel @Inject constructor(
    private val stackRepository: StackRepository,
    private val userManager: UserManager) :
    ViewModel() {
    var chatrooms = MutableLiveData<MutableList<Chatroom>>()
    fun getChatroom() {
        viewModelScope.launch {
            userManager.user?.id?.let { stackRepository.getChatroom(it, updateLiveDataCallback) }
        }
    }

    private val updateLiveDataCallback: (MutableList<Chatroom>) -> Unit = { chatroomsFromFirestore ->
        updateLiveData(chatroomsFromFirestore)
    }
    private fun updateLiveData(chatroomsFromFireStore: MutableList<Chatroom>){
        chatrooms.postValue(chatroomsFromFireStore)
    }
}