package com.example.stack

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import kotlin.properties.Delegates
@HiltAndroidApp
class StackApplication : Application() {
    companion object {
        var instance: StackApplication by Delegates.notNull()
    }
    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}