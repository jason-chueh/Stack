package com.example.stack

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.stack.data.StackRepository
import com.example.stack.data.dataclass.User
import com.example.stack.login.UserManager
import com.example.stack.util.CurrentFragmentType
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val stackRepository: StackRepository): ViewModel() {
    val isLoggedIn
        get() = UserManager.isLoggedIn

    private val _user = MutableLiveData<User>()

    val user: LiveData<User>
        get() = _user

    private val _navigateToLoginSuccess = MutableLiveData<User>()

    val currentFragmentType = MutableLiveData<CurrentFragmentType>()
    val navigateToLoginSuccess: LiveData<User>
        get() = _navigateToLoginSuccess

    fun setupUser(user: User) {

        _user.value = user
    }

    fun navigateToLoginSuccess(user: User) {
        _navigateToLoginSuccess.value = user
    }


}