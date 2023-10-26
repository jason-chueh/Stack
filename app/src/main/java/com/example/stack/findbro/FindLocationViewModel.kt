package com.example.stack.findbro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stack.data.StackRepository
import com.example.stack.data.dataclass.User
import com.example.stack.login.UserManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FindLocationViewModel @Inject constructor(
    private val stackRepository: StackRepository,
    private val userManager: UserManager) :
    ViewModel() {
    fun upsertUser(lat: String, long: String) {
        viewModelScope.launch(Dispatchers.IO) {
            userManager.updateUser(userManager.user?.copy(gymLatitude = lat, gymLongitude = long))
            userManager.user?.let { stackRepository.upsertUser(it) }
            userManager.user?.let { stackRepository.uploadUserToFireStore(it) }
        }
    }
}