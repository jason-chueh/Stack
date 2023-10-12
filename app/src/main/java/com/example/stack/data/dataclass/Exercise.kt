package com.example.stack.data.dataclass

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import kotlinx.parcelize.Parcelize
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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
    val equipment: String,
    @TypeConverters(StringListConverter::class)
    @ColumnInfo(name = "secondary_muscle")
    val secondaryMuscles: List<String> = listOf(),
    @TypeConverters(StringListConverter::class)
    @ColumnInfo(name = "instructions")
    val instructions: List<String> = listOf()
): Parcelable
@Parcelize
data class ExerciseFromFireStore(
    val id: String="",

    val name: String="",

    val target: String= "",

    val gifUrl: String = "",

    val bodyPart: String = "",

    val equipment: String = "",

    val stability: Int=0,

    val instructions: List<String> = listOf(),

    val secondaryMuscles: List<String> = listOf()

): Parcelable


data class ExerciseWithCheck(
    val id: String,
    val name: String,
    val target: String,
    val gifUrl: String,
    val bodyPart: String,
    val equipment: String,
    val secondaryMuscles: List<String> = listOf(),
    val instructions: List<String> = listOf(),
    val check: Boolean = false
)

fun Exercise.toExerciseWithCheck()=ExerciseWithCheck(
    id = id,
    name = name,
    target = target,
    gifUrl = gifUrl,
    bodyPart = bodyPart,
    equipment = equipment,
    secondaryMuscles = secondaryMuscles,
    instructions = instructions,
    check = false
)


fun ExerciseFromFireStore.toExercise()=Exercise(
    id = id,
    name = name,
    target = target,
    gifUrl = gifUrl,
    bodyPart = bodyPart,
    equipment = equipment,
    instructions = instructions,
    secondaryMuscles = secondaryMuscles
)

class StringListConverter {
    @TypeConverter
    fun fromString(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(value, listType)
    }
    @TypeConverter
    fun toString(value: List<String>): String {
        return Gson().toJson(value)
    }
}
