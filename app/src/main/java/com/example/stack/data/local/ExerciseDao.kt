package com.example.stack.data.local

import androidx.room.Dao
import androidx.room.Upsert
import com.example.stack.data.dataclass.Exercise

@Dao
interface ExerciseDao {
    @Upsert
    fun upsertExerciseList(exerciseList: List<Exercise>)
}