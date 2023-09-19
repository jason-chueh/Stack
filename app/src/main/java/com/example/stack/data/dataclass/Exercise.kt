package com.example.stack.data.dataclass

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
@Entity(tableName = "exercise_table")
@Parcelize
data class Exercise(
    @PrimaryKey
    @ColumnInfo(name = "exercise_id")
    val id: String,
    @ColumnInfo(name = "exercise_name")
    val name: String,
    @ColumnInfo(name = "exercise_target")
    val target: String,
    @ColumnInfo(name = "exercise_gif_url")
    val gifUrl: String,
    @ColumnInfo(name = "exercise_body_part")
    val bodyPart: String,
    @ColumnInfo(name = "exercise_equipment")
    val equipment: String
): Parcelable
@Parcelize
data class ExerciseFromFireStore(
    val id: String="",

    val name: String="",

    val target: String= "",

    val gifUrl: String = "",

    val bodyPart: String = "",

    val equipment: String = "",

    val stability: Int=0
): Parcelable

fun ExerciseFromFireStore.toExercise()=Exercise(
    id = id,
    name = name,
    target = target,
    gifUrl = gifUrl,
    bodyPart = bodyPart,
    equipment = equipment
)