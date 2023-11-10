package com.example.stack.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stack.data.StackRepository
import com.example.stack.data.dataclass.LoadApiStatus
import com.example.stack.data.dataclass.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val stackRepository: StackRepository,
    private val userManager: UserManager
) : ViewModel() {

    private val _user = MutableLiveData<User>()
    private var auth: FirebaseAuth = Firebase.auth

    val user: LiveData<User>
        get() = _user

    // Handle navigation to login success
    private val _navigateToLoginSuccess = MutableLiveData<User>()

    val navigateToLoginSuccess: LiveData<User>
        get() = _navigateToLoginSuccess

    private val _leave = MutableLiveData<Boolean>()
    val leave: LiveData<Boolean>
        get() = _leave

    // status: The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<LoadApiStatus>()

    val status: LiveData<LoadApiStatus>
        get() = _status

    // error: The internal MutableLiveData that stores the error of the most recent request
    private val _error = MutableLiveData<String>()

    val error: LiveData<String>
        get() = _error

    // two-way data binding
    val email = MutableLiveData<String>()

    // two-way data binding
    val password = MutableLiveData<String>()

    val name = MutableLiveData<String>()


    private val _registerErrorToast = MutableLiveData(false)

    val registerErrorToast: LiveData<Boolean>
        get() = _registerErrorToast


    fun nativeLogin(displayName: String) {
        viewModelScope.launch {
            if (email.value != null && password.value != null) {
                auth.signInWithEmailAndPassword(email.value!!, password.value!!)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            val user = auth.currentUser
                            if (userManager.user == null && name.value != null) {
                                userManager.updateUser(User(user!!.uid, name.value!!, user.email!!))
                                viewModelScope.launch(Dispatchers.IO) {
                                    userManager.user?.let { stackRepository.uploadUserToFireStore(it) }
                                }
                            }
                            viewModelScope.launch(Dispatchers.IO) {
                                userManager.user?.let { stackRepository.upsertUser(it) }
                            }
                            leave()
                        } else {
                            createAccount(email.value!!, password.value!!, displayName)
                        }
                    }
            }
        }
    }

    private fun createAccount(email: String, password: String, displayName: String) {
        // [START create_user_with_email]
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    val user = auth.currentUser
                    userManager.updateUser(User(user!!.uid, displayName, email))
                    viewModelScope.launch(Dispatchers.IO) {
                        userManager.user?.let {
                            stackRepository.upsertUser(it)
                            stackRepository.uploadUserToFireStore(it)
                        }
                    }
                    leave()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.i("login", "createUserWithEmail:failure", task.exception)
                    _registerErrorToast.value = true
                }
            }
    }

    fun login() {
        _status.value = LoadApiStatus.LOADING
    }

    fun leave() {
        _leave.value = true
    }

    fun onLeaveCompleted() {
        _leave.value = null
    }

    fun onRegisterErrorToastCompleted() {
        _registerErrorToast.value = false
    }
}
