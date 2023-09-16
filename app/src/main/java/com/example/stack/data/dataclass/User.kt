package com.example.stack.data.dataclass

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val id: Int = -1,
    val name: String = "",
    val email: String = "",
    val picture: String? = null
) : Parcelable