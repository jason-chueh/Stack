package com.example.stack.data.dataclass

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import kotlinx.parcelize.Parcelize

@Entity(tableName = "template")
@Parcelize
data class Template(
    @PrimaryKey
    @ColumnInfo(name = "template_id")
    var templateId: String,
    @ColumnInfo(name = "user_id")
    var userId: String,
    @ColumnInfo(name = "template_name")
    var templateName: String
): Parcelable
// find a templateId by userId, then search all the exercise records by templateId

@Entity(tableName = "template_exercise_records", primaryKeys = ["template_id","exercise_id"])
@Parcelize
data class TemplateExerciseRecord(
    @ColumnInfo(name = "template_id")
    var templateId: String,
    @ColumnInfo(name = "exercise_name")
    var exerciseName: String,
    @ColumnInfo(name = "exercise_id")
    var exerciseId: String,
    @TypeConverters(RepsAndWeightsConverter::class)
    @ColumnInfo(name = "reps_and_weights")
    var repsAndWeights: MutableList<RepsAndWeights>
): Parcelable

fun TemplateExerciseRecord.toTemplateExerciseWithCheck(userId: String, startTime: Long)=ExerciseRecordWithCheck(
    userId = userId,
    startTime = startTime,
    exerciseId = exerciseId,
    exerciseName = exerciseName,
    repsAndWeights = repsAndWeights.map{it.toRepsAndWeightsWithCheck()}.toMutableList()
)

