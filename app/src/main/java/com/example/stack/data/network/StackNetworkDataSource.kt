package com.example.stack.data.network

import android.util.Log
import com.chaquo.python.Python
import com.example.stack.data.dataclass.Chat
import com.example.stack.data.dataclass.ChatGptRequest
import com.example.stack.data.dataclass.ChatGptResponse
import com.example.stack.data.dataclass.Chatroom
import com.example.stack.data.dataclass.ChatroomFromFireStore
import com.example.stack.data.dataclass.DistanceMatrixResponse
import com.example.stack.data.dataclass.Exercise
import com.example.stack.data.dataclass.User
import com.example.stack.data.dataclass.VideoItem
import com.example.stack.data.dataclass.toChatroom
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class StackNetworkDataSource @Inject constructor(
    private val exerciseApiService: ExerciseApiService,
    private val gptApiService: ChatGptApiService,
    private val mapApiService: DistanceMatrixService,
    private val python: Python
) : NetworkDataSource {

    private val moduleTranscript = python.getModule("Transcript")
    private val moduleYoutubeSearch = python.getModule("YoutubeSearch")
    private val db = Firebase.firestore
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

    override suspend fun getDataFromExercise(): QuerySnapshot {
        return db.collection("Exercises").get().await()
    }

    override suspend fun setExerciseDataToFireStore(exercise: Exercise) {
        db.collection("Exercises").document(exercise.id).set(exercise)
            .addOnSuccessListener {
                Log.i(
                    "api",
                    "DocumentSnapshot successfully written!"
                )
            }
            .addOnFailureListener { e -> Log.i("api", "Error writing document") }
    }

    override suspend fun createChatroomAtFireStore(chatroom: Chatroom, callBack: (Chatroom)->Unit) {
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
                Log.i("chatroom","isEmpty")
                //the chatroom is not existed, create one for it
                val docRef = ref.document()
                val newChatroom = chatroom.copy(roomId = docRef.id)
                docRef.set(newChatroom)
                callBack(newChatroom)
            }
            else{

                val searchedChatroom =  querySnapshot.documents[0].toObject<ChatroomFromFireStore>()
                Log.i("chatroom","$searchedChatroom")
                searchedChatroom?.toChatroom()?.let { callBack(it) }
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

    override suspend fun updateChatroom(chatroom: Chatroom) {
        val ref = db.collection("chatroom").document(chatroom.roomId)
        ref.set(chatroom).addOnSuccessListener {
            Log.i("chatroom", "update chatroom success!")
        }.addOnFailureListener {
            Log.i("chatroom", "$it")
        }
    }

    override suspend fun uploadUserToFireStore(user: User) {
        Log.i("login","uploadUserToFireStore Called")
        db.collection("user").document(user.id).set(user).addOnSuccessListener {
            Log.i("user", "$user")
            Log.i("user", "upload fireStore success!")
        }.addOnFailureListener {
            Log.i("user", "$it")
        }
    }

    override suspend fun getUsersFromFireStore(callback: (MutableList<User>) -> Unit) {
        db.collection("user").get().addOnSuccessListener {
            Log.i("googleMaps","success")
            val resultList = mutableListOf<User>()
            for(doc in it){
                resultList.add(doc.toObject<User>())
            }
            callback(resultList)
        }.addOnFailureListener {
            Log.i("googleMaps","$it")
        }
    }

    override suspend fun sendChatMessageToFireStore(chat: Chat) {
        val docRef = db.collection("chat").document()
        docRef.set(chat.copy(chatId = docRef.id)).addOnSuccessListener {
            Log.i("chat", "chat written! $chat")
        }.addOnFailureListener {
            Log.i("chat", "$it")
        }
    }
}