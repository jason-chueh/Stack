package com.example.stack.data.dataclass

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Chat(
    var chatId: String = "",
    var chatroomId: String = "",
    var senderId: String = "",
    var receiverId: String = "",
    var message: String = "",
    var sendTime: Long = 0
):Parcelable