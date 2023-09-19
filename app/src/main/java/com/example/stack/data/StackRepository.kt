package com.example.stack.data

import com.example.stack.data.dataclass.ChatGptRequest
import com.example.stack.data.dataclass.ChatGptResponse
import com.example.stack.data.dataclass.Exercise
import com.example.stack.data.dataclass.ExerciseRecord
import com.example.stack.data.dataclass.ExerciseYoutube
import com.example.stack.data.dataclass.User
import com.example.stack.data.dataclass.VideoItem

interface StackRepository {
    suspend fun test2(): List<ExerciseRecord>

    suspend fun upsertUser(user: User)

    suspend fun upsertExerciseList(exercises: List<Exercise>)

    suspend fun refreshExerciseDb()

    suspend fun exerciseApiToDb()

    suspend fun getYoutubeVideo(exerciseId: String, exerciseName: String): List<VideoItem>

    suspend fun getTranscript(youtubeId: String): String?

    suspend fun searchYoutubeByExercise(exerciseId: String): List<ExerciseYoutube>

    suspend fun searchYoutubeByYoutubeId(youtubeId: String): ExerciseYoutube

    suspend fun insertYoutubeList(youtubeList: List<ExerciseYoutube>)

    suspend fun updateYoutubeData(exerciseYoutube: ExerciseYoutube)

    suspend fun getInstruction(chatGptRequest: ChatGptRequest):ChatGptResponse
}