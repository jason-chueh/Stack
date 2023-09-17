package com.example.stack.data.network

import com.example.stack.data.dataclass.Exercise
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

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
object ExerciseApi {

    val retrofitService: ExerciseApiService by lazy { retrofit.create(ExerciseApiService::class.java) }
}