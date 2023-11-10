package com.example.stack.data

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.stack.BuildConfig
import com.example.stack.data.dataclass.Chat
import com.example.stack.data.dataclass.ChatGptRequest
import com.example.stack.data.dataclass.ChatGptResponse
import com.example.stack.data.dataclass.Chatroom
import com.example.stack.data.dataclass.DistanceMatrixResponse
import com.example.stack.data.dataclass.Exercise
import com.example.stack.data.dataclass.ExerciseFromFireStore
import com.example.stack.data.dataclass.ExerciseRecord
import com.example.stack.data.dataclass.ExerciseWithExerciseYoutube
import com.example.stack.data.dataclass.ExerciseYoutube
import com.example.stack.data.dataclass.Template
import com.example.stack.data.dataclass.TemplateExerciseRecord
import com.example.stack.data.dataclass.User
import com.example.stack.data.dataclass.VideoItem
import com.example.stack.data.dataclass.Workout
import com.example.stack.data.dataclass.toExercise
import com.example.stack.data.local.LocalDataSource
import com.example.stack.data.network.NetworkDataSource
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DefaultRepository @Inject constructor(
    private val networkDataSource: NetworkDataSource,
    private val localDataSource: LocalDataSource,
) : StackRepository {

    override suspend fun test2(): List<ExerciseWithExerciseYoutube>? {
        return localDataSource.test2()
    }

    override suspend fun upsertUser(user: User) {
        localDataSource.upsertUser(user)
    }

    override fun getUsers(): LiveData<List<User>> {
        return localDataSource.getUsers()
    }

    override suspend fun upsertExerciseList(exercises: List<Exercise>) {
        localDataSource.upsertExerciseList(exercises)
    }

    //Get all the exercise data from fireStore and refresh dataBase
    override suspend fun refreshExerciseDb() {
        try {
            withContext(Dispatchers.IO) {

                val exerciseList = mutableListOf<Exercise>()

                val result = networkDataSource.getDataFromExercise()

                for (document in result) {
                    exerciseList.add(document.toObject<ExerciseFromFireStore>().toExercise())
                }
                Log.i("api", "refreshExerciseDb: $exerciseList")

                localDataSource.upsertExerciseList(exerciseList)
            }
        } catch (e: Exception) {
            Log.e("api", "refreshExerciseDb: $e")
        }
    }

    override suspend fun exerciseApiToDatabase() {
        withContext(Dispatchers.IO) {
            try {
                for (k in listOf(
                    "barbell",
                    "body weight",
                    "cable",
                    "dumbbell",
                    "hammer",
                    "leverage machine",
                    "smith machine"
                )) {
                    val result = networkDataSource.getExerciseByEquipment(
                        k, BuildConfig.EXERCISE_KEY,
                        "exercisedb.p.rapidapi.com",
                        200
                    )
                    Log.i("api", "$result")
                    Log.i("api", "${result.size}")
                    localDataSource.upsertExerciseList(result)
                }
            } catch (e: java.lang.Exception) {
                refreshExerciseDb()
                Log.i("api", "$e")
            }
        }
    }

    //Get all the exercise from API, and send it to fireStore
    override suspend fun exerciseApiToFireStore() {
        try {
            for (k in listOf(
                "barbell",
                "body weight",
                "cable",
                "dumbbell",
                "hammer",
                "leverage machine",
                "smith machine"
            )) {
                val result = networkDataSource.getExerciseByEquipment(
                    k, BuildConfig.EXERCISE_KEY,
                    "exercisedb.p.rapidapi.com",
                    100
                )
                Log.i("api", "$result")
                Log.i("api", "${result.size}")
                for (i in result) {
                    networkDataSource.setExerciseDataToFireStore(i)
                }
            }
        } catch (e: java.lang.Exception) {
            Log.i("api", "$e")
        }
    }

    override suspend fun getAllExercise(): List<Exercise> {
        return localDataSource.getAllExercise()
    }

    override suspend fun getExerciseById(exerciseId: String): Exercise? {
        return localDataSource.getExerciseById(exerciseId)
    }

    override suspend fun getYoutubeVideo(
        exerciseId: String,
        exerciseName: String
    ): List<VideoItem> {
        return networkDataSource.getYoutubeVideo(exerciseId, exerciseName)
    }

    override suspend fun getTranscript(youtubeId: String): String {
        return networkDataSource.getTranscript(youtubeId)
    }

    override suspend fun searchYoutubeByExercise(exerciseId: String): List<ExerciseYoutube> {
        return localDataSource.searchYoutubeByExercise(exerciseId)
    }

    override suspend fun searchYoutubeByYoutubeId(youtubeId: String): ExerciseYoutube {
        return localDataSource.searchYoutubeByYoutubeId(youtubeId)
    }

    override suspend fun insertYoutubeList(youtubeList: List<ExerciseYoutube>) {
        localDataSource.insertYoutubeList(youtubeList)
    }

    override suspend fun updateYoutubeData(exerciseYoutube: ExerciseYoutube) {
        localDataSource.updateYoutubeData(exerciseYoutube)
    }

    override suspend fun getInstruction(chatGptRequest: ChatGptRequest): ChatGptResponse? {
        return try {
            networkDataSource.generateChatResponse(chatGptRequest)
        } catch (e: Exception) {
            Log.i("chatgpt", "$e")
            null
        }
    }

    override suspend fun getDistanceMatrix(
        origins: String,
        destinations: String,
        apiKey: String
    ): DistanceMatrixResponse? {
        return try {
            networkDataSource.getDistanceMatrix(origins, destinations, apiKey)
        } catch (e: Exception) {
            Log.i("googleMap", "$e")
            null
        }
    }

    override suspend fun upsertTemplate(template: Template) {
        try {
            localDataSource.upsertTemplate(template)
        } catch (e: Exception) {
            Log.i("template", "$e")
        }
    }

    override suspend fun searchTemplateIdListByUserId(userId: String): List<String> {
        return localDataSource.searchTemplateIdListByUserId(userId)
    }

    override suspend fun searchTemplatesByUserId(userId: String): List<Template> {
        return localDataSource.searchTemplatesByUserId(userId)
    }

    override suspend fun upsertTemplateExerciseRecords(templateExerciseRecordsList: List<TemplateExerciseRecord>) {
        localDataSource.upsertTemplateExerciseRecords(templateExerciseRecordsList)
    }

    override suspend fun upsertTemplateExerciseRecord(templateExerciseRecord: TemplateExerciseRecord) {
        localDataSource.upsertTemplateExerciseRecord(templateExerciseRecord)
    }

    override suspend fun getTemplateExerciseRecordListByTemplateId(templateId: String): List<TemplateExerciseRecord> {
        return localDataSource.getTemplateExerciseRecordListByTemplateId(templateId)
    }

    override suspend fun createChatroomAtFireStore(
        chatroom: Chatroom,
        callBack: (Chatroom) -> Unit
    ) {
        networkDataSource.createChatroomAtFireStore(chatroom, callBack)
    }

    override fun searchChatroomByUserId(userId1: String, userId2: String) {

    }

    override suspend fun getChatroom(userId: String, callBack: (MutableList<Chatroom>) -> Unit) {
        networkDataSource.getChatroom(userId, callBack)
    }

    override suspend fun updateChatroom(chatroom: Chatroom) {
        networkDataSource.updateChatroom(chatroom)
    }

    override suspend fun sendChatMessageToFireStore(chat: Chat) {
        networkDataSource.sendChatMessageToFireStore(chat)
    }

    override suspend fun findAllWorkoutById(userId: String): List<Workout> {
        return localDataSource.findAllWorkoutById(userId)
    }

    override suspend fun upsertWorkout(workout: Workout) {
        localDataSource.upsertWorkout(workout)
    }

    override suspend fun upsertExerciseRecordList(exerciseRecordList: List<ExerciseRecord>) {
        localDataSource.upsertExerciseRecordList(exerciseRecordList)
    }

    override suspend fun getAllExercisesByUserId(userId: String): List<ExerciseRecord> {
        return localDataSource.getAllExerciseRecordsByUserId(userId)
    }

    override suspend fun uploadUserToFireStore(user: User) {
        networkDataSource.uploadUserToFireStore(user)
    }

    override suspend fun deleteAllTemplate() {
        localDataSource.deleteAllTemplate()
    }

    override suspend fun deleteYoutubeById(id: String) {
        localDataSource.deleteYoutubeById(id)
    }

    override suspend fun deleteTemplateByTemplateId(templateId: String) {
        localDataSource.deleteTemplateByTemplateId(templateId = templateId)
    }

    override suspend fun getUsersFromFireStore(callback: (MutableList<User>) -> Unit) {
        networkDataSource.getUsersFromFireStore(callback)
    }

}