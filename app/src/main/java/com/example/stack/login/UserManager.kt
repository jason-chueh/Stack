package com.example.stack.login

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.stack.StackApplication
import com.example.stack.data.dataclass.User
import com.google.gson.Gson

object UserManager {

    private const val USER_DATA = "user_data"
    private const val USER_TOKEN = "user_token"

    var isTraining: Boolean = false

    var user: User?
        get() {
            val sharedPreferences = StackApplication.instance.getSharedPreferences(USER_DATA, Context.MODE_PRIVATE)
            val userJson = sharedPreferences.getString(USER_TOKEN, null)
            return userJson?.toUserObject()
        }
        set(value) {
            Log.i("login","set user: $value")
            val sharedPreferences = StackApplication.instance.getSharedPreferences(USER_DATA, Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString(USER_TOKEN, value?.toJsonString())
            editor.apply()
        }

    val isLoggedIn: Boolean
        get() = user != null

    /**
     * Clear the [userToken] and the [user]/[_user] data
     */
    fun clear() {
        user = null
        val sharedPreferences = StackApplication.instance.getSharedPreferences("USER_DATA", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }
}

// Extension functions to convert User to JSON and vice versa
fun User.toJsonString(): String {
    return Gson().toJson(this)
}

fun String?.toUserObject(): User? {
    if (this == null) return null
    return Gson().fromJson(this, User::class.java)
}






