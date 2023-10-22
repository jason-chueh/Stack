package com.example.stack.data.network

import android.util.Log
import com.chaquo.python.Python
import com.example.stack.data.dataclass.ChatGptRequest
import com.example.stack.data.dataclass.ChatGptResponse
import com.example.stack.data.dataclass.DistanceMatrixResponse
import com.example.stack.data.dataclass.Exercise
import com.example.stack.data.dataclass.VideoItem
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import javax.inject.Inject

class StackNetworkDataSource @Inject constructor(
    private val exerciseApiService: ExerciseApiService,
    private val gptApiService: ChatGptApiService,
    private val mapApiService: DistanceMatrixService,
    private val python: Python
) : NetworkDataSource {

    private val moduleTranscript = python.getModule("Transcript")
    private val moduleYoutubeSearch = python.getModule("YoutubeSearch")
    override suspend fun generateChatResponse(requestBody: ChatGptRequest): ChatGptResponse {
        return gptApiService.generateChatResponse(requestBody)
    }

    override suspend fun getDistanceMatrix(
        origins: String,
        destinations: String,
        apiKey: String
    ): DistanceMatrixResponse {
        return mapApiService.getDistanceMatrix(origins, destinations, apiKey)
    }

    override suspend fun getExerciseByEquipment(
        equipment: String,
        key: String,
        host: String,
        limit: Int
    ): List<Exercise> {
        return exerciseApiService.getExerciseByEquipment(equipment, key, host, limit)
    }

    override suspend fun getTranscript(youtubeId: String): String {
        val result: String
        try {
            result =
                moduleTranscript.callAttr("getTranscript", youtubeId).toJava(String::class.java)
        } catch (e: Exception) {
            Log.i("python", "$e")
            return ""
        }
        return result
    }

    override suspend fun getYoutubeVideo(
        exerciseId: String,
        exerciseName: String
    ): List<VideoItem> {
        var videoList = listOf<VideoItem>()
        try {
            val resultJson = moduleYoutubeSearch.callAttr(
                "youtubeSearch",
                "$exerciseName tutorial"
            )
                .toJava(String::class.java)
            val moshi = Moshi.Builder().build()
            val listType = Types.newParameterizedType(List::class.java, VideoItem::class.java)
            val adapter = moshi.adapter<List<VideoItem>>(listType)
            videoList = adapter.fromJson(resultJson)!!
            Log.i("python", "$videoList")

            Log.i("python", "${videoList.size}")
        } catch (e: Exception) {
            Log.e("python", "$e")
        }
        return videoList
    }
}