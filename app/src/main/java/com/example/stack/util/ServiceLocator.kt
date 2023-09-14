package com.example.stack.util

import android.content.Context
import androidx.annotation.VisibleForTesting
import com.example.stack.repository.StackDataSource
import com.example.stack.repository.StackRepository

object ServiceLocator {

    @Volatile
    var stackRepository: StackRepository? = null
        @VisibleForTesting set

    fun provideTasksRepository(context: Context): StackRepository {
        synchronized(this) {
            return stackRepository
                ?: createStylishRepository(context)
        }
    }

    private fun createStylishRepository(context: Context): StackRepository {
        return StackDataSource
    }
}