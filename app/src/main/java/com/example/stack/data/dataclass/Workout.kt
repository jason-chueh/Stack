package com.example.stack.data.dataclass

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import kotlinx.parcelize.Parcelize

@Entity(primaryKeys = ["user_id","start_time"])
@Parcelize
data class Workout(
    @ColumnInfo(name = "user_id")
    val userId: String,
    @ColumnInfo(name = "workout_name")
    val workoutName: String,
    @ColumnInfo(name = "start_time")
    val startTime: Long,
    @ColumnInfo(name = "end_time")
    val endTime: Long
): Parcelable
