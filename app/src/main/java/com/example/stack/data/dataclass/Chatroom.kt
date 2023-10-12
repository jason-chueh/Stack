package com.example.stack.data.dataclass

import android.os.Message
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Chatroom(
    var roomId: String = "",
    var userId1: String = "",
    var userId2: String = "",
    var userName: List<String> = listOf(),
    var userPic: List<String?> = listOf(),
    var lastMessage: String = "",
    var lastMessageTime: Long = 0
): Parcelable


data class ChatroomFromFireStore(
    var roomId: String = "",
    var userId1: String = "",
    var userId2: String = "",
    var userName: List<String> = listOf(),
    var userPic: List<String> = listOf(),
    var lastMessage: String = "",
    var lastMessageTime: Long = 0,
    var stability: Int = 0
)

fun ChatroomFromFireStore.toChatroom() = Chatroom(
    roomId = roomId,
    userId1 = userId1,
    userId2 = userId2,
    userName = userName,
    userPic = userPic,
    lastMessage = lastMessage,
    lastMessageTime = lastMessageTime
)