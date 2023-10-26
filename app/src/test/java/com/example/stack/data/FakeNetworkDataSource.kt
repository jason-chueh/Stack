package com.example.stack.data

import com.example.stack.data.dataclass.ChatGptRequest
import com.example.stack.data.dataclass.ChatGptResponse
import com.example.stack.data.dataclass.DistanceMatrixResponse
import com.example.stack.data.dataclass.Exercise
import com.example.stack.data.dataclass.VideoItem
import com.example.stack.data.network.NetworkDataSource

class FakeNetworkDataSource: NetworkDataSource {
    override suspend fun generateChatResponse(requestBody: ChatGptRequest): ChatGptResponse {
        TODO("Not yet implemented")
    }

    override suspend fun getDistanceMatrix(
        origins: String,
        destinations: String,
        apiKey: String
    ): DistanceMatrixResponse {
        TODO("Not yet implemented")
    }

    override suspend fun getExerciseByEquipment(
        equipment: String,
        key: String,
        host: String,
        limit: Int
    ): List<Exercise> {
        TODO("Not yet implemented")
    }

    override suspend fun getTranscript(youtubeId: String): String {
        return "mock transcript return"
    }

    override suspend fun getYoutubeVideo(
        exerciseId: String,
        exerciseName: String
    ): List<VideoItem> {
        TODO("Not yet implemented")
    }
}