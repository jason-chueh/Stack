package com.example.stack.data.network

import com.example.stack.BuildConfig
import com.example.stack.data.dataclass.ChatGptRequest
import com.example.stack.data.dataclass.ChatGptResponse
import com.example.stack.data.dataclass.DistanceMatrixResponse
import com.example.stack.data.dataclass.Exercise
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

private const val HOST_NAME = "exercisedb.p.rapidapi.com"
private const val BASE_URL = "https://$HOST_NAME/exercises/"

internal val moshi = Moshi.Builder()
    .addLast(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()
interface ExerciseApiService{
    @GET("equipment/{equipment}")
    suspend fun getExerciseByEquipment(
        @Path("equipment") equipment: String,
        @Header("X-RapidAPI-Key") key: String,
        @Header("X-RapidAPI-Host") host: String): List<Exercise>
}



val retrofitGpt = Retrofit.Builder()
    .baseUrl("https://api.openai.com/")
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .build()

interface ChatGptApiService {
    @Headers(
        "Authorization: Bearer ${BuildConfig.GPT_KEY}", // Replace with your API key
        "Content-Type: application/json"
    )
    @POST("v1/completions")
    suspend fun generateChatResponse(@Body requestBody: ChatGptRequest): ChatGptResponse
}

val retrofitGoogleMap = Retrofit.Builder()
    .baseUrl("https://maps.googleapis.com/maps/api/")
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .build()

interface DistanceMatrixService {
    @GET("distancematrix/json")
    suspend fun getDistanceMatrix(
        @Query("origins") origins: String,
        @Query("destinations") destinations: String,
        @Query("key") apiKey: String
    ): DistanceMatrixResponse
}


object StackApi {

    val retrofitService: ExerciseApiService by lazy { retrofit.create(ExerciseApiService::class.java) }
    val retrofitGptService: ChatGptApiService by lazy{ retrofitGpt.create(ChatGptApiService::class.java)}
    val distanceMatrixService: DistanceMatrixService by lazy{ retrofitGoogleMap.create(DistanceMatrixService::class.java)}
}