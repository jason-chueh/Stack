package com.example.stack.data.dataclass

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
@Entity(tableName = "user_table")
@Parcelize
data class User(
    @PrimaryKey
    @ColumnInfo(name = "user_id")
    var id: String = "",
    @ColumnInfo(name = "user_name")
    var name: String = "",
    @ColumnInfo(name = "user_email")
    var email: String = "",
    @ColumnInfo(name = "user_picture")
    val picture: String? = null,
    @ColumnInfo(name = "user_gym_latitude")
    val gymLatitude: String? = null,
    @ColumnInfo(name = "user_gym_longitude")
    val gymLongitude: String? = null
) : Parcelable


