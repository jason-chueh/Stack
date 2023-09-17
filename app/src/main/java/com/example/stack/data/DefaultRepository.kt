package com.example.stack.data

import android.util.Log
import com.example.stack.data.dataclass.Exercise
import com.example.stack.data.dataclass.ExerciseFromFireStore
import com.example.stack.data.dataclass.ExerciseRecord
import com.example.stack.data.dataclass.RepsAndWeights
import com.example.stack.data.dataclass.User
import com.example.stack.data.dataclass.toExercise
import com.example.stack.data.local.ExerciseDao
import com.example.stack.data.local.ExerciseRecordDao
import com.example.stack.data.local.UserDao
import com.example.stack.data.network.ExerciseApi
import com.example.stack.data.network.NetworkDataSource
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DefaultRepository @Inject constructor(
    private val networkDataSource: NetworkDataSource,
    private val userDao: UserDao,
    private val exerciseDao: ExerciseDao,
    private val exerciseRecordDao: ExerciseRecordDao
) : StackRepository {

    val db = Firebase.firestore
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
                val result = ExerciseApi.retrofitService.getExerciseByEquipment(
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
}