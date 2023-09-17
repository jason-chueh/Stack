package com.example.stack.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Update
import androidx.room.Delete
import androidx.room.Query
import androidx.room.OnConflictStrategy
import com.example.stack.data.dataclass.ExerciseRecord

@Dao
interface ExerciseRecordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExerciseRecord(exerciseRecord: ExerciseRecord)

    @Update
    suspend fun updateExerciseRecord(exerciseRecord: ExerciseRecord)

    @Delete
    suspend fun deleteExerciseRecord(exerciseRecord: ExerciseRecord)

    @Query("SELECT * FROM exercise_records WHERE user_id = :userId")
    suspend fun getExerciseRecordsByUserId(userId: String): List<ExerciseRecord>

    // Add more queries as needed
}