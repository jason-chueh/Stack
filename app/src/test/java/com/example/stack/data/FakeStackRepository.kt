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

class FakeStackRepository: StackRepository {
    override suspend fun test2(): List<ExerciseRecord> {
        return mutableListOf(ExerciseRecord("123",123,"123","squat", mutableListOf()))
    }

    override suspend fun upsertUser(user: User) {
        TODO("Not yet implemented")
    }

    override fun getUsers(): LiveData<List<User>> {
        TODO("Not yet implemented")
    }

    override suspend fun upsertExerciseList(exercises: List<Exercise>) {
        TODO("Not yet implemented")
    }

    override suspend fun refreshExerciseDb() {
        TODO("Not yet implemented")
    }

    override suspend fun exerciseApiToFireStore() {
        TODO("Not yet implemented")
    }

    override suspend fun exerciseApiToDatabase() {
        TODO("Not yet implemented")
    }

    override suspend fun getAllExercise(): List<Exercise> {
        TODO("Not yet implemented")
    }

    override suspend fun getExerciseById(exerciseId: String): Exercise? {
        TODO("Not yet implemented")
    }

    override suspend fun getYoutubeVideo(
        exerciseId: String,
        exerciseName: String
    ): List<VideoItem> {
        TODO("Not yet implemented")
    }

    override suspend fun getTranscript(youtubeId: String): String? {
        TODO("Not yet implemented")
    }

    override suspend fun searchYoutubeByExercise(exerciseId: String): List<ExerciseYoutube> {
        TODO("Not yet implemented")
    }

    override suspend fun searchYoutubeByYoutubeId(youtubeId: String): ExerciseYoutube {
        TODO("Not yet implemented")
    }

    override suspend fun insertYoutubeList(youtubeList: List<ExerciseYoutube>) {
        TODO("Not yet implemented")
    }

    override suspend fun updateYoutubeData(exerciseYoutube: ExerciseYoutube) {
        TODO("Not yet implemented")
    }

    override suspend fun getInstruction(chatGptRequest: ChatGptRequest): ChatGptResponse? {
        TODO("Not yet implemented")
    }

    override suspend fun getDistanceMatrix(
        origins: String,
        destinations: String,
        apiKey: String
    ): DistanceMatrixResponse? {
        TODO("Not yet implemented")
    }

    override suspend fun upsertTemplate(template: Template) {
        TODO("Not yet implemented")
    }

    override suspend fun searchTemplatesByUserId(userId: String): List<Template> {
        TODO("Not yet implemented")
    }

    override suspend fun searchTemplateIdListByUserId(userId: String): List<String> {
        TODO("Not yet implemented")
    }

    override suspend fun upsertTemplateExerciseRecord(templateExerciseRecords: TemplateExerciseRecord) {
        TODO("Not yet implemented")
    }

    override suspend fun upsertTemplateExerciseRecord(templateExerciseRecordsList: List<TemplateExerciseRecord>) {
        TODO("Not yet implemented")
    }

    override suspend fun getTemplateExerciseRecordListByTemplateId(templateId: String): List<TemplateExerciseRecord> {
        TODO("Not yet implemented")
    }

    override fun createChatroomAtFireStore(chatroom: Chatroom, callBack: (Chatroom) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun searchChatroomByUserId(userId1: String, userId2: String) {
        TODO("Not yet implemented")
    }

    override suspend fun getChatroom(userId: String, callBack: (MutableList<Chatroom>) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun updateChatroom(chatroom: Chatroom) {
        TODO("Not yet implemented")
    }

    override fun sendChatMessageToFireStore(chat: Chat) {
        TODO("Not yet implemented")
    }

    override suspend fun upsertWorkout(workout: Workout) {
        TODO("Not yet implemented")
    }

    override suspend fun findAllWorkoutById(userId: String): List<Workout> {
        TODO("Not yet implemented")
    }

    override suspend fun upsertExerciseRecordList(exerciseRecordList: List<ExerciseRecord>) {
        TODO("Not yet implemented")
    }

    override suspend fun getAllExercisesByUserId(userId: String): List<ExerciseRecord> {
        TODO("Not yet implemented")
    }

    override fun uploadUserToFireStore(user: User) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAllTemplate() {
        TODO("Not yet implemented")
    }

    override suspend fun deleteYoutubeById(id: String) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteTemplateByTemplateId(templateId: String) {
        TODO("Not yet implemented")
    }
}