package com.example.stack.data.network

import com.example.stack.data.dataclass.Chat
import com.example.stack.data.dataclass.ChatGptRequest
import com.example.stack.data.dataclass.ChatGptResponse
import com.example.stack.data.dataclass.Chatroom
import com.example.stack.data.dataclass.DistanceMatrixResponse
import com.example.stack.data.dataclass.Exercise
import com.example.stack.data.dataclass.User
import com.example.stack.data.dataclass.VideoItem
import com.google.firebase.firestore.QuerySnapshot
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface NetworkDataSource {

    suspend fun generateChatResponse(requestBody: ChatGptRequest): ChatGptResponse

    suspend fun getDistanceMatrix(
        origins: String,
        destinations: String,
        apiKey: String
    ): DistanceMatrixResponse

    suspend fun getExerciseByEquipment(
        equipment: String,
        key: String,
        host: String,
        limit: Int
    ): List<Exercise>

    suspend fun getTranscript(youtubeId: String): String

    suspend fun getYoutubeVideo(
        exerciseId: String,
        exerciseName: String
    ): List<VideoItem>

    suspend fun getDataFromExercise(): QuerySnapshot

    suspend fun setExerciseDataToFireStore(exercise: Exercise)

    suspend fun createChatroomAtFireStore(chatroom: Chatroom, callBack: (Chatroom)->Unit)

    suspend fun getChatroom(userId: String, callBack: (MutableList<Chatroom>) -> Unit)

    suspend fun updateChatroom(chatroom: Chatroom)

    suspend fun uploadUserToFireStore(user: User)

    suspend fun getUsersFromFireStore(callback: (MutableList<User>) -> Unit)

    suspend fun sendChatMessageToFireStore(chat: Chat)
}