package com.example.stack.data

import com.example.stack.data.dataclass.User

interface StackRepository {
    fun test2()

    suspend fun upsertUser(user: User)
}