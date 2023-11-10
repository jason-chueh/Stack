package com.example.stack.data.dataclass

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import kotlinx.parcelize.Parcelize
@Entity(tableName = "exercise_youtube_table")
@Parcelize
data class ExerciseYoutube(
    @PrimaryKey
    @ColumnInfo(name = "youtube_id")
    val youtubeId: String,

    @ColumnInfo(name = "exercise_id")
    val exerciseId: String,

    @ColumnInfo(name = "youtube_title")
    val youtubeTitle: String,

    @ColumnInfo(name = "views")
    val views: String,

    @ColumnInfo(name = "published_time")
    val publishedTime: String,

    @ColumnInfo(name = "thumbnail_url")
    val thumbnailUrl: String,

    @ColumnInfo(name = "transcript")
    var transcript: String? = null,

    @ColumnInfo(name = "instruction")
    var instruction: String? = null
) : Parcelable


data class ExerciseWithExerciseYoutube(

    @ColumnInfo(name = "exercise_id")
    val id: String,

    @ColumnInfo(name = "exercise_name")
    val name: String,

    @ColumnInfo(name = "youtube_title")
    val youtubeTitle: String,
)