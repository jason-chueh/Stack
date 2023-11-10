package com.example.stack.data.local

import androidx.lifecycle.LiveData
import com.example.stack.data.dataclass.Exercise
import com.example.stack.data.dataclass.ExerciseRecord
import com.example.stack.data.dataclass.ExerciseWithExerciseYoutube
import com.example.stack.data.dataclass.ExerciseYoutube
import com.example.stack.data.dataclass.Template
import com.example.stack.data.dataclass.TemplateExerciseRecord
import com.example.stack.data.dataclass.User
import com.example.stack.data.dataclass.Workout

interface LocalDataSource {
    suspend fun upsertUser(user: User)

    fun getUsers(): LiveData<List<User>>

    suspend fun upsertExerciseList(exercises: List<Exercise>)

    suspend fun getAllExercise(): List<Exercise>

    suspend fun getExerciseById(exerciseId: String): Exercise?

    suspend fun searchYoutubeByExercise(exerciseId: String): List<ExerciseYoutube>

    suspend fun searchYoutubeByYoutubeId(youtubeId: String): ExerciseYoutube

    suspend fun insertYoutubeList(youtubeList: List<ExerciseYoutube>)

    suspend fun updateYoutubeData(exerciseYoutube: ExerciseYoutube)

    suspend fun upsertTemplate(template: Template)

    suspend fun searchTemplateIdListByUserId(userId: String): List<String>

    suspend fun searchTemplatesByUserId(userId: String): List<Template>

    suspend fun upsertTemplateExerciseRecords(templateExerciseRecordsList: List<TemplateExerciseRecord>)

    suspend fun upsertTemplateExerciseRecord(templateExerciseRecord: TemplateExerciseRecord)

    suspend fun getTemplateExerciseRecordListByTemplateId(templateId: String): List<TemplateExerciseRecord>

    suspend fun findAllWorkoutById(userId: String): List<Workout>

    suspend fun upsertWorkout(workout: Workout)

    suspend fun upsertExerciseRecordList(exerciseRecordList: List<ExerciseRecord>)

    suspend fun getAllExerciseRecordsByUserId(userId: String): List<ExerciseRecord>

    suspend fun deleteYoutubeById(id: String)

    suspend fun deleteTemplateByTemplateId(templateId: String)

    suspend fun deleteAllTemplate()

    suspend fun test2(): List<ExerciseWithExerciseYoutube>?
}