package com.example.stack.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.stack.data.dataclass.Exercise
import com.example.stack.data.dataclass.Workout

@Dao
interface WorkoutDao {
    @Upsert
    fun upsertWorkout(workout: Workout)

    @Upsert
    fun upsertWorkoutList(workoutList: List<Workout>)

    @Query("SELECT * FROM workout_table WHERE user_id == :userId")
    fun findAllWorkoutById(userId: String):List<Workout>


}