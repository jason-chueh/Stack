package com.example.stack.data.dataclass

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.parcelize.Parcelize

@Entity(tableName = "exercise_records", primaryKeys = ["user_id","start_time","exercise_id"])
@Parcelize
data class ExerciseRecord(
    @ColumnInfo(name = "user_id")
    val userId: String,
    @ColumnInfo(name = "start_time")
    val startTime: Long,
    @ColumnInfo(name = "exercise_id")
    val exerciseId: String,
    @ColumnInfo(name = "exercise_name")
    val exerciseName: String,
    @TypeConverters(RepsAndWeightsConverter::class)
    @ColumnInfo(name = "repsAndWeights")
    var repsAndWeights: MutableList<RepsAndWeights>
): Parcelable

data class ExerciseRecordWithCheck(
    val userId: String,
    val startTime: Long,
    val exerciseId: String,
    val exerciseName: String,
    var repsAndWeights: MutableList<RepsAndWeightsWithCheck>
)

@Parcelize
data class RepsAndWeights(
    val reps: Int,
    val weight: Int
): Parcelable


data class RepsAndWeightsWithCheck(
    val reps: Int,
    val weight: Int,
    var check: Boolean = false
)

fun RepsAndWeights.toRepsAndWeightsWithCheck()=
    RepsAndWeightsWithCheck(
        reps = reps,
        weight = weight,
        check = false
    )

fun ExerciseRecord.toExerciseRecordWithCheck()=
    ExerciseRecordWithCheck(
        userId = userId,
        startTime = startTime,
        exerciseId = exerciseId,
        exerciseName = exerciseName,
        repsAndWeights = repsAndWeights.map{it.toRepsAndWeightsWithCheck()}.toMutableList()
    )


class RepsAndWeightsConverter {
    @TypeConverter
    fun fromRepsAndWeightsList(repsAndWeights: List<RepsAndWeights>?): String? {
        return Gson().toJson(repsAndWeights)
    }

    @TypeConverter
    fun toRepsAndWeightsList(repsAndWeightsJson: String?): List<RepsAndWeights>? {
        val type = object : TypeToken<List<RepsAndWeights>>() {}.type
        return Gson().fromJson(repsAndWeightsJson, type)
    }
}