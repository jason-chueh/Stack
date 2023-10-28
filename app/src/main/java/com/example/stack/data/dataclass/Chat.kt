package com.example.stack.data.dataclass

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Chat(
    val chatId: String = "",
    val chatroomId: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val message: String = "",
    val sendTime: Long = 0
):Parcelable