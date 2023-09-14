package com.example.stack

import android.app.Application
import com.example.stack.repository.StackRepository
import com.example.stack.util.ServiceLocator
import kotlin.properties.Delegates

class StackApplication : Application() {

    // Depends on the flavor,
    val stackRepository: StackRepository
        get() = ServiceLocator.provideTasksRepository(this)

    companion object {
        var instance: StackApplication by Delegates.notNull()
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}