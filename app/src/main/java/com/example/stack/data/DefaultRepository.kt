package com.example.stack.data

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.stack.BuildConfig
import com.example.stack.data.dataclass.Chat
import com.example.stack.data.dataclass.ChatGptRequest
import com.example.stack.data.dataclass.ChatGptResponse
import com.example.stack.data.dataclass.Chatroom
import com.example.stack.data.dataclass.ChatroomFromFireStore
import com.example.stack.data.dataclass.DistanceMatrixResponse
import com.example.stack.data.dataclass.Exercise
import com.example.stack.data.dataclass.ExerciseFromFireStore
import com.example.stack.data.dataclass.ExerciseRecord
import com.example.stack.data.dataclass.ExerciseYoutube
import com.example.stack.data.dataclass.Template
import com.example.stack.data.dataclass.TemplateExerciseRecord
import com.example.stack.data.dataclass.User
import com.example.stack.data.dataclass.VideoItem
import com.example.stack.data.dataclass.Workout
import com.example.stack.data.dataclass.toChatroom
import com.example.stack.data.dataclass.toExercise
import com.example.stack.data.local.ExerciseDao
import com.example.stack.data.local.ExerciseRecordDao
import com.example.stack.data.local.ExerciseYoutubeDao
import com.example.stack.data.local.TemplateDao
import com.example.stack.data.local.TemplateExerciseRecordDao
import com.example.stack.data.local.UserDao
import com.example.stack.data.local.UserInfoDao
import com.example.stack.data.local.WorkoutDao
import com.example.stack.data.network.StackApi
import com.example.stack.data.network.NetworkDataSource
import com.example.stack.data.network.PythonManager
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DefaultRepository @Inject constructor(
    private val networkDataSource: NetworkDataSource,
    private val userDao: UserDao,
    private val exerciseDao: ExerciseDao,
    private val exerciseRecordDao: ExerciseRecordDao,
    private val exerciseYoutubeDao: ExerciseYoutubeDao,
    private val templateDao: TemplateDao,
    private val templateExerciseRecordDao: TemplateExerciseRecordDao,
    private val workoutDao: WorkoutDao,
    private val userInfoDao: UserInfoDao
) : StackRepository {

    val db = Firebase.firestore
    val py = PythonManager.getInstance()
    private val moduleTranscript = py.getModule("Transcript")
    private val moduleYoutubeSearch = py.getModule("YoutubeSearch")
    override suspend fun test2(): List<ExerciseRecord> {
        var result = listOf<ExerciseRecord>()
        try {
//            exerciseRecordDao.insertExerciseRecord(
//                ExerciseRecord(
//                    userId = "user123",
//                    startTime = System.currentTimeMillis(),
//                    exerciseId = "exercise1",
//                    repsAndWeights = listOf(
//                        RepsAndWeights(reps = 10, weight = 20),
//                        RepsAndWeights(reps = 12, weight = 25),
//                        RepsAndWeights(reps = 8, weight = 30)
//                    )
//                )
//            )
            result = exerciseRecordDao.getExerciseRecordsByUserId("user123")
        } catch (e: Exception) {
            Log.i("gson", "$e")
        }
        return result
    }

    override suspend fun upsertUser(user: User) {
        withContext(Dispatchers.IO) {
            userDao.upsertUser(user)
        }
    }

    override fun getUsers(): LiveData<List<User>> {
        return userDao.getUsers()
    }

    override suspend fun upsertExerciseList(exercises: List<Exercise>) {
        withContext(Dispatchers.IO) {
            exerciseDao.upsertExerciseList(exercises)
        }
    }

    //Get all the exercise data from fireStore and refresh dataBase
    override suspend fun refreshExerciseDb() {
        try {
            withContext(Dispatchers.IO) {
                val exerciseList = mutableListOf<Exercise>()
                val result = db.collection("Exercises").get().await()

                for (document in result) {
                    exerciseList.add(document.toObject<ExerciseFromFireStore>().toExercise())
                }
                Log.i("api", "refreshExerciseDb: $exerciseList")
                Log.i("api", "refreshExerciseDb: ${exerciseList.size}")

                // Use the IO dispatcher for the database operation

                exerciseDao.upsertExerciseList(exerciseList)
            }
        } catch (e: Exception) {
            Log.e("api", "refreshExerciseDb: $e")
        }
    }

    //Get all the exercise from API, and send it to fireStore
    override suspend fun exerciseApiToFireStore() {
        try {
            for (k in listOf<String>(
                "barbell",
                "body weight",
                "cable",
                "dumbbell",
                "hammer",
                "leverage machine",
                "smith machine"
            )) {
                val result = StackApi.retrofitService.getExerciseByEquipment(
                    k, BuildConfig.EXERCISE_KEY,
                    "exercisedb.p.rapidapi.com",
                    100
                )
                Log.i("api", "$result")
                Log.i("api", "${result.size}")
                for (i in result) {
                    db.collection("Exercises").document(i.id).set(i)
                        .addOnSuccessListener {
                            Log.i(
                                "api",
                                "DocumentSnapshot successfully written!"
                            )
                        }
                        .addOnFailureListener { e -> Log.i("api", "Error writing document") }
                }
            }
        } catch (e: java.lang.Exception) {
            Log.i("api", "$e")
        }
    }

    override suspend fun getAllExercise(): List<Exercise> {
        return exerciseDao.getAllExercise()
    }

    override suspend fun getExerciseById(exerciseId: String): Exercise? {
        return exerciseDao.getExerciseById(exerciseId)
    }

    override suspend fun getYoutubeVideo(
        exerciseId: String,
        exerciseName: String
    ): List<VideoItem> {
        var videoList = listOf<VideoItem>()
        try {
            val result_json = moduleYoutubeSearch.callAttr("youtubeSearch",
                "$exerciseName tutorial"
            )
                .toJava(String::class.java)
            val moshi = Moshi.Builder().build()
            val listType = Types.newParameterizedType(List::class.java, VideoItem::class.java)
            val adapter = moshi.adapter<List<VideoItem>>(listType)
            videoList = adapter.fromJson(result_json)!!
            Log.i("python", "$videoList")

            Log.i("python", "${videoList.size}")
        } catch (e: Exception) {
            Log.e("python", "$e")
        }
        return videoList
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

    override suspend fun searchYoutubeByExercise(exerciseId: String): List<ExerciseYoutube> {
        return exerciseYoutubeDao.getYoutubeByExerciseId(exerciseId)
    }

    override suspend fun searchYoutubeByYoutubeId(youtubeId: String): ExerciseYoutube {
        return exerciseYoutubeDao.getYoutubeByYoutubeId(youtubeId)
    }

    override suspend fun insertYoutubeList(youtubeList: List<ExerciseYoutube>) {
        exerciseYoutubeDao.insertYoutube(youtubeList)
    }

    override suspend fun updateYoutubeData(exerciseYoutube: ExerciseYoutube) {
        exerciseYoutubeDao.updateYoutube(exerciseYoutube)
    }

    override suspend fun getInstruction(chatGptRequest: ChatGptRequest): ChatGptResponse? {
        try {
            return StackApi.retrofitGptService.generateChatResponse(chatGptRequest)
        } catch (e: Exception) {
            Log.i("chatgpt", "$e")
            return null
        }
    }

    override suspend fun getDistanceMatrix(
        origins: String,
        destinations: String,
        apiKey: String
    ): DistanceMatrixResponse? {
        try {
            return StackApi.distanceMatrixService.getDistanceMatrix(origins, destinations, apiKey)
        } catch (e: Exception) {
            Log.i("googleMap", "$e")
            return null
        }
    }

    override suspend fun upsertTemplate(template: Template) {
        try {
            templateDao.upsertTemplate(template)
        } catch (e: Exception) {
            Log.i("template", "$e")
        }
    }

    override suspend fun searchTemplateIdListByUserId(userId: String): List<String> {
        return templateDao.searchTemplateIdListByUserId(userId)
    }

    override suspend fun searchTemplatesByUserId(userId: String): List<Template> {
        return templateDao.searchTemplatesListByUserId(userId)
    }

    override suspend fun upsertTemplateExerciseRecord(templateExerciseRecordsList: List<TemplateExerciseRecord>) {
        return templateExerciseRecordDao.upsertTemplateExerciseRecord(templateExerciseRecordsList)
    }

    override suspend fun upsertTemplateExerciseRecord(templateExerciseRecord: TemplateExerciseRecord) {
        return templateExerciseRecordDao.upsertTemplateExerciseRecord(templateExerciseRecord)
    }

    override suspend fun getTemplateExerciseRecordListByTemplateId(templateId: String): List<TemplateExerciseRecord> {
        return templateExerciseRecordDao.getTemplateExerciseRecordListByTemplateId(templateId)
    }

    override fun createChatroomAtFireStore(chatroom: Chatroom) {
        val ref = db.collection("chatroom")
        val query = ref.where(
            Filter.or(
                Filter.and(
                    Filter.equalTo("userId1", chatroom.userId1),
                    Filter.equalTo("userId2", chatroom.userId2)
                ),
                Filter.and(
                    Filter.equalTo("userId2", chatroom.userId1),
                    Filter.equalTo("userId1", chatroom.userId2)
                )
            )
        )

        query.get().addOnSuccessListener { querySnapshot ->

            if (querySnapshot.isEmpty) {
                //the chatroom is not existed, create one for it
                val docRef = ref.document()
                docRef.set(chatroom.copy(roomId = docRef.id))
            }

        }.addOnFailureListener { exception ->
            Log.i("chatroom", "$exception")
        }
    }

    override suspend fun getChatroom(userId: String, callBack: (MutableList<Chatroom>) -> Unit) {
        val chatroomList = mutableListOf<Chatroom>()
        withContext(Dispatchers.IO) {
            val ref = db.collection("chatroom")
            val query = ref.where(
                Filter.or(
                    Filter.equalTo("userId1", userId),
                    Filter.equalTo("userId2", userId)
                )
            )
            query.get().addOnSuccessListener { querySnapShot ->
                if (!querySnapShot.isEmpty) {
                    for (document in querySnapShot) {
                        chatroomList.add(document.toObject<ChatroomFromFireStore>().toChatroom())
                    }
                    callBack(chatroomList)
                }
            }
        }
    }

    override fun updateChatroom(chatroom: Chatroom) {
        val ref = db.collection("chatroom").document(chatroom.roomId)
        ref.set(chatroom).addOnSuccessListener {
            Log.i("chatroom","update chatroom success!")
        }.addOnFailureListener {
            Log.i("chatroom","$it")
        }
    }

    override fun sendChatMessageToFireStore(chat: Chat) {
        val docRef = db.collection("chat").document()
        docRef.set(chat.copy(chatId = docRef.id)).addOnSuccessListener {
            Log.i("chat","chat written! $chat")
        }.addOnFailureListener {
            Log.i("chat","$it")
        }
    }

    override suspend fun findAllWorkoutById(userId: String): List<Workout> {
        return workoutDao.findAllWorkoutById(userId)
    }

    override suspend fun upsertWorkout(workout: Workout) {
        workoutDao.upsertWorkout(workout)
    }

    override suspend fun upsertExerciseRecordList(exerciseRecordList: List<ExerciseRecord>) {
        exerciseRecordDao.upsertExerciseRecordList(exerciseRecordList)
    }

    override suspend fun getAllExercisesByUserId(userId: String): List<ExerciseRecord> {
        return exerciseRecordDao.getExerciseRecordsByUserId(userId)
    }

    override fun uploadUserToFireStore(user: User) {
        db.collection("user").document(user.id).set(user).addOnSuccessListener {
            Log.i("user","$user")
            Log.i("user","upload fireStore success!")
        }.addOnFailureListener {
            Log.i("user","$it")
        }
    }

    override suspend fun deleteAllTemplate() {
        templateDao.deleteAllTemplate()
        templateExerciseRecordDao.deleteAllTemplateExerciseRecords()
    }

    override suspend fun deleteYoutubeById(id: String) {
        exerciseYoutubeDao.deleteYoutubeById(id)
    }

    override suspend fun deleteTemplateByTemplateId(templateId: String){
        templateDao.deleteTemplateByTemplateId(templateId = templateId)
    }


}