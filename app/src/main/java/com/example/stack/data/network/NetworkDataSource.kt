package com.example.stack.data.network

import com.example.stack.data.dataclass.ChatGptRequest
import com.example.stack.data.dataclass.ChatGptResponse
import com.example.stack.data.dataclass.DistanceMatrixResponse
import com.example.stack.data.dataclass.Exercise
import com.example.stack.data.dataclass.VideoItem
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
}