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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val stackRepository: StackRepository) : ViewModel() {

    private val _user = MutableLiveData<User>()
    private var auth: FirebaseAuth = Firebase.auth


    val user: LiveData<User>
        get() = _user

    // Handle navigation to login success
    private val _navigateToLoginSuccess = MutableLiveData<User>()

    val navigateToLoginSuccess: LiveData<User>
        get() = _navigateToLoginSuccess

    // Handle leave login
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

    private val _loginErrorToast = MutableLiveData<Boolean>(false)

    val loginErrorToast: LiveData<Boolean>
        get() = _loginErrorToast

    private val _registerErrorToast = MutableLiveData<Boolean>(false)

    val registerErrorToast: LiveData<Boolean>
        get() = _registerErrorToast

    private val _invalidCheckout = MutableLiveData<Int>()

    val invalidCheckout: LiveData<Int>
        get() = _invalidCheckout

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    fun nativeLogin(){
        viewModelScope.launch {
            if(email.value!=null && password.value!=null){
            auth.signInWithEmailAndPassword(email.value!!, password.value!!)
                .addOnCompleteListener() { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        val user = auth.currentUser
                        if(UserManager.user == null && name.value != null){
                            UserManager.user = User(user!!.uid, name.value!!, user!!.email!!)
                            UserManager.user?.let { stackRepository.uploadUserToFireStore(it) }
                        }
                        coroutineScope.launch {
                            UserManager.user?.let { stackRepository.upsertUser(it) }
                            UserManager.user?.let { stackRepository.uploadUserToFireStore(it) }
                        }
                        leave()
                    } else {
                        _loginErrorToast.value = true
                        createAccount(email.value!!, password.value!!)
                        // If sign in fails, display a message to the user.
                        Log.i("signin", "createUserWithEmail:failure")
                    }
                }
            }
        }
    }

    private fun createAccount(email: String, password: String) {
        // [START create_user_with_email]
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener() { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.i("login", "createUserWithEmail:success")
                    val user = auth.currentUser
                    if(UserManager.user == null){
                        UserManager.user = User(user!!.uid, user.displayName!!, user.email!!)
                    }
                    coroutineScope.launch {
                        UserManager.user?.let { stackRepository.upsertUser(it) }
                        UserManager.user?.let{stackRepository.uploadUserToFireStore(it)}
                    }

                    leave()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.i("login", "createUserWithEmail:failure", task.exception)
                    _registerErrorToast.value = true
                }
            }
        // [END create_user_with_email]
    }

    /**
     * When the [ViewModel] is finished, we cancel our coroutine [viewModelJob], which tells the
     * Retrofit service to stop.
     */
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }





    private fun loginStylish(email: String, password: String) {

    }

    /**
     * Login Stylish by Facebook: Step 1. Register FB Login Callback
     */
    fun login() {
        _status.value = LoadApiStatus.LOADING
    }


    /**
     * Login Stylish by Facebook: Step 2. Login Facebook
     */

    fun leave() {
        _leave.value = true
    }

    fun onLeaveCompleted() {
        _leave.value = null
    }

    fun onLoginErrorToastCompleted(){
        _loginErrorToast.value = false
    }

    fun onRegisterErrorToastCompleted(){
        _registerErrorToast.value = false
    }

    fun nothing() {}


}
