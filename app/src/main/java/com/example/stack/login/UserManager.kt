package com.example.stack.login

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.stack.data.dataclass.User

object UserManager {

    private const val USER_DATA = "user_data"
    private const val USER_TOKEN = "user_token"

    private val _user = MutableLiveData<User>(User())

    val user: LiveData<User>
        get() = _user

    /**
     * It can be use to check login status directly
     */
    val isLoggedIn: Boolean
        get() = _user.value?.id != -1

    /**
     * Clear the [userToken] and the [user]/[_user] data
     */
    fun clear() {
        _user.value = null
    }

    private var lastChallengeTime: Long = 0
    private var challengeCount: Int = 0
    private const val CHALLENGE_LIMIT = 23

    /**
     * Winter is coming
     */

}