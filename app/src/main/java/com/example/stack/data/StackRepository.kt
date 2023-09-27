package com.example.stack.data

import androidx.lifecycle.LiveData
import com.example.stack.data.dataclass.ChatGptRequest
import com.example.stack.data.dataclass.ChatGptResponse
import com.example.stack.data.dataclass.DistanceMatrixResponse
import com.example.stack.data.dataclass.Exercise
import com.example.stack.data.dataclass.ExerciseRecord
import com.example.stack.data.dataclass.ExerciseYoutube
import com.example.stack.data.dataclass.Template
import com.example.stack.data.dataclass.TemplateExerciseRecord
import com.example.stack.data.dataclass.User
import com.example.stack.data.dataclass.VideoItem

interface StackRepository {
    suspend fun test2(): List<ExerciseRecord>

    suspend fun upsertUser(user: User)

    fun getUsers(): LiveData<List<User>>

    suspend fun upsertExerciseList(exercises: List<Exercise>)

    suspend fun refreshExerciseDb()

    suspend fun exerciseApiToDb()

    suspend fun getYoutubeVideo(exerciseId: String, exerciseName: String): List<VideoItem>

    suspend fun getTranscript(youtubeId: String): String?

    suspend fun searchYoutubeByExercise(exerciseId: String): List<ExerciseYoutube>

    suspend fun searchYoutubeByYoutubeId(youtubeId: String): ExerciseYoutube

    suspend fun insertYoutubeList(youtubeList: List<ExerciseYoutube>)

    suspend fun updateYoutubeData(exerciseYoutube: ExerciseYoutube)

    suspend fun getInstruction(chatGptRequest: ChatGptRequest):ChatGptResponse?

    suspend fun getDistanceMatrix(origins: String, destinations: String, apiKey: String): DistanceMatrixResponse?

    suspend fun upsertTemplate(template: Template)

    suspend fun searchTemplateIdListByUserId(userId: String): List<String>

    suspend fun upsertTemplateExerciseRecord(templateExerciseRecords: TemplateExerciseRecord)

    suspend fun upsertTemplateExerciseRecord(templateExerciseRecordsList: List<TemplateExerciseRecord>)

    suspend fun getTemplateExerciseRecordListByTemplateId(templateId: String): List<TemplateExerciseRecord>

}