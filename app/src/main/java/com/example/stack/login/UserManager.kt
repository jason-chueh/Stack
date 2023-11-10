package com.example.stack.login

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.stack.StackApplication
import com.example.stack.data.dataclass.User
import com.google.gson.Gson

class UserManager {

    private val USER_DATA = "user_data"
    private val USER_TOKEN = "user_token"

    var isTraining: Boolean = false
        private set
    private val sharedPreferences: SharedPreferences = StackApplication.instance.getSharedPreferences(USER_DATA, Context.MODE_PRIVATE)

    var user: User? = null
        get() {
            if (field == null) {
                // Initialize user from SharedPreferences during the first access
                val userJson = sharedPreferences.getString(USER_TOKEN, null)
                field = userJson?.toUserObject()
                Log.i("login", "init user")
            }
            Log.i("login", "get user")
            return field
        }
        private set(value) {
            Log.i("login", "set user: $value")
            val editor = sharedPreferences.edit()
            editor.putString(USER_TOKEN, value?.toJsonString())
            editor.apply()
            field = value
        }
    fun updateUser(updatedUser: User?){
        user = updatedUser
    }

    fun updateIsTraining(boolean: Boolean){
        isTraining = boolean
    }
    val isLoggedIn: Boolean
        get() = user != null


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






