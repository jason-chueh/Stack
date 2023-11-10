package com.example.stack.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.example.stack.StackApplication

object Util {

    fun getString(resourceId: Int): String {
        return StackApplication.instance.getString(resourceId)
    }

    fun getColor(resourceId: Int): Int {
        return StackApplication.instance.getColor(resourceId)
    }
}
