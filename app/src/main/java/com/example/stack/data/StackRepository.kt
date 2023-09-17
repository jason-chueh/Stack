package com.example.stack.data

import com.example.stack.data.dataclass.Exercise
import com.example.stack.data.dataclass.ExerciseRecord
import com.example.stack.data.dataclass.User

interface StackRepository {
    suspend fun test2(): List<ExerciseRecord>

    suspend fun upsertUser(user: User)
    suspend fun upsertExerciseList(exercises: List<Exercise>)
    suspend fun refreshExerciseDb()

    suspend fun exerciseApiToDb()
}