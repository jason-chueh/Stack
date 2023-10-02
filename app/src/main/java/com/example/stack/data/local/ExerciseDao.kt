package com.example.stack.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.stack.data.dataclass.Exercise

@Dao
interface ExerciseDao {
    @Upsert
    fun upsertExerciseList(exerciseList: List<Exercise>)
    @Query("SELECT * FROM exercise_table")
    fun getAllExercise(): List<Exercise>
    @Query("SELECT * FROM exercise_table WHERE exercise_id = :exerciseId")
    fun getExerciseById(exerciseId: String): Exercise?
}