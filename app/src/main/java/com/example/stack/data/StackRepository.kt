package com.example.stack.data

import androidx.lifecycle.LiveData
import com.example.stack.data.dataclass.Chat
import com.example.stack.data.dataclass.ChatGptRequest
import com.example.stack.data.dataclass.ChatGptResponse
import com.example.stack.data.dataclass.Chatroom
import com.example.stack.data.dataclass.DistanceMatrixResponse
import com.example.stack.data.dataclass.Exercise
import com.example.stack.data.dataclass.ExerciseRecord
import com.example.stack.data.dataclass.ExerciseYoutube
import com.example.stack.data.dataclass.Template
import com.example.stack.data.dataclass.TemplateExerciseRecord
import com.example.stack.data.dataclass.User
import com.example.stack.data.dataclass.VideoItem
import com.example.stack.data.dataclass.Workout

interface StackRepository {
    suspend fun test2(): List<ExerciseRecord>

    suspend fun upsertUser(user: User)

    fun getUsers(): LiveData<List<User>>

    suspend fun upsertExerciseList(exercises: List<Exercise>)

    suspend fun refreshExerciseDb()

    suspend fun exerciseApiToFireStore()

    suspend fun exerciseApiToDatabase()

    suspend fun getAllExercise(): List<Exercise>

    suspend fun getExerciseById(exerciseId: String): Exercise?

    suspend fun getYoutubeVideo(exerciseId: String, exerciseName: String): List<VideoItem>

    suspend fun getTranscript(youtubeId: String): String?

    suspend fun searchYoutubeByExercise(exerciseId: String): List<ExerciseYoutube>

    suspend fun searchYoutubeByYoutubeId(youtubeId: String): ExerciseYoutube

    suspend fun insertYoutubeList(youtubeList: List<ExerciseYoutube>)

    suspend fun updateYoutubeData(exerciseYoutube: ExerciseYoutube)

    suspend fun getInstruction(chatGptRequest: ChatGptRequest): ChatGptResponse?

    suspend fun getDistanceMatrix(
        origins: String,
        destinations: String,
        apiKey: String
    ): DistanceMatrixResponse?

    suspend fun upsertTemplate(template: Template)

    suspend fun searchTemplatesByUserId(userId: String): List<Template>

    suspend fun searchTemplateIdListByUserId(userId: String): List<String>

    suspend fun upsertTemplateExerciseRecord(templateExerciseRecords: TemplateExerciseRecord)

    suspend fun upsertTemplateExerciseRecord(templateExerciseRecordsList: List<TemplateExerciseRecord>)

    suspend fun getTemplateExerciseRecordListByTemplateId(templateId: String): List<TemplateExerciseRecord>

    fun createChatroomAtFireStore(chatroom: Chatroom, callBack: (Chatroom)->Unit)

    fun searchChatroomByUserId(userId1: String, userId2: String)

    suspend fun getChatroom(userId: String, callBack: (MutableList<Chatroom>) -> Unit)

    fun updateChatroom(chatroom: Chatroom)

    fun sendChatMessageToFireStore(chat: Chat)

    suspend fun upsertWorkout(workout: Workout)

    suspend fun findAllWorkoutById(userId: String): List<Workout>

    suspend fun upsertExerciseRecordList(exerciseRecordList: List<ExerciseRecord>)

    suspend fun getAllExercisesByUserId(userId: String): List<ExerciseRecord>

    fun uploadUserToFireStore(user: User)

    suspend fun deleteAllTemplate()

    suspend fun deleteYoutubeById(id: String)

    suspend fun deleteTemplateByTemplateId(templateId: String)
}