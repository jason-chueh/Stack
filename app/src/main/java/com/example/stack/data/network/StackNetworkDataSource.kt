package com.example.stack.data.network

import android.util.Log
import javax.inject.Inject

class StackNetworkDataSource @Inject constructor(): NetworkDataSource {
    override fun test() {
        Log.i("test","hello")
    }
}