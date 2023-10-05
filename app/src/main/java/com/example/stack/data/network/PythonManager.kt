package com.example.stack.data.network

import android.content.Context
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform

object PythonManager {
    private var pythonInitialized = false

    fun initialize(context: Context) {
        if (!pythonInitialized) {
            Python.start(AndroidPlatform(context))
            pythonInitialized = true
        }
    }
    fun getInstance(): Python {
        if (!pythonInitialized) {
            throw IllegalStateException("PythonInterpreterManager is not initialized. Call initialize() first.")
        }

        return Python.getInstance()
    }
}