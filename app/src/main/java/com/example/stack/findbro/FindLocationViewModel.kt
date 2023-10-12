package com.example.stack.findbro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stack.data.StackRepository
import com.example.stack.data.dataclass.User
import com.example.stack.login.UserManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FindLocationViewModel @Inject constructor(private val stackRepository: StackRepository) :
    ViewModel() {
    fun upsertUser(lat: String, long: String) {
        viewModelScope.launch {
            UserManager.user = UserManager.user?.copy(gymLatitude = lat, gymLongitude = long)
            UserManager.user?.let { stackRepository.upsertUser(it) }
            UserManager.user?.let { stackRepository.uploadUserToFireStore(it) }
        }
    }
}