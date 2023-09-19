package com.example.stack.data

import android.util.Log
import com.example.stack.data.dataclass.ChatGptRequest
import com.example.stack.data.dataclass.ChatGptResponse
import com.example.stack.data.dataclass.Exercise
import com.example.stack.data.dataclass.ExerciseFromFireStore
import com.example.stack.data.dataclass.ExerciseRecord
import com.example.stack.data.dataclass.ExerciseYoutube
import com.example.stack.data.dataclass.User
import com.example.stack.data.dataclass.VideoItem
import com.example.stack.data.dataclass.toExercise
import com.example.stack.data.local.ExerciseDao
import com.example.stack.data.local.ExerciseRecordDao
import com.example.stack.data.local.ExerciseYoutubeDao
import com.example.stack.data.local.UserDao
import com.example.stack.data.network.StackApi
import com.example.stack.data.network.NetworkDataSource
import com.example.stack.data.network.PythonManager
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
    private val exerciseYoutubeDao: ExerciseYoutubeDao
) : StackRepository {

    val db = Firebase.firestore
    val py = PythonManager.getInstance()
    val moduleTranscript = py.getModule("Transcript")
    val moduleYoutubeSearch = py.getModule("YoutubeSearch")
    override suspend fun test2():List<ExerciseRecord> {
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

    override suspend fun upsertExerciseList(exercises: List<Exercise>) {
        withContext(Dispatchers.IO) {
            exerciseDao.upsertExerciseList(exercises)
        }
    }

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

    override suspend fun exerciseApiToDb() {
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
                    k, "e0d0cc3fd4msh947dd855c1dc15dp148aa7jsn860ea01e79c1",
                    "exercisedb.p.rapidapi.com"
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

    override suspend fun getYoutubeVideo(exerciseId: String, exerciseName: String): List<VideoItem> {
            var videoList = listOf<VideoItem>()
            try {
                val result_json = moduleYoutubeSearch.callAttr("youtubeSearch", exerciseName)
                    .toJava(String::class.java)
                val moshi = Moshi.Builder().build()
                val listType = Types.newParameterizedType(List::class.java, VideoItem::class.java)
                val adapter = moshi.adapter<List<VideoItem>>(listType)
                videoList = adapter.fromJson(result_json)!!
                Log.i("python", "$videoList")
//                videoList.forEach {
//                    try {
//                        moduleTranscript.callAttr("getTranscript", it.id)
//                        it.haveTranscript = 1
//                        Log.i("python","change!")
//                    } catch (e: Exception) {
//                        Log.e("python", "$e")
//                    }
//                }
                Log.i("python","${videoList.size}")
            } catch (e: Exception) {
                Log.e("python", "$e")
            }
            return videoList
    }


    override suspend fun getTranscript(youtubeId: String): String{
        val result: String
        try{
            result = moduleTranscript.callAttr("getTranscript", youtubeId).toJava(String::class.java)
        }catch(e: Exception){
            Log.i("python","$e")
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

    override suspend fun getInstruction(chatGptRequest: ChatGptRequest): ChatGptResponse {
        return StackApi.retrofitGptService.generateChatResponse(chatGptRequest)
    }
}