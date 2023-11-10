package com.example.stack.data.local

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.stack.data.dataclass.Exercise
import com.example.stack.data.dataclass.ExerciseRecord
import com.example.stack.data.dataclass.ExerciseWithExerciseYoutube
import com.example.stack.data.dataclass.ExerciseYoutube
import com.example.stack.data.dataclass.Template
import com.example.stack.data.dataclass.TemplateExerciseRecord
import com.example.stack.data.dataclass.User
import com.example.stack.data.dataclass.Workout
import javax.inject.Inject

class StackLocalDataSource @Inject constructor(
    private val userDao: UserDao,
    private val exerciseDao: ExerciseDao,
    private val exerciseRecordDao: ExerciseRecordDao,
    private val exerciseYoutubeDao: ExerciseYoutubeDao,
    private val templateDao: TemplateDao,
    private val templateExerciseRecordDao: TemplateExerciseRecordDao,
    private val workoutDao: WorkoutDao,
    private val userInfoDao: UserInfoDao
) : LocalDataSource {
    override suspend fun upsertUser(user: User) {
        userDao.upsertUser(user)
    }

    override fun getUsers(): LiveData<List<User>> {
        return userDao.getUsers()
    }

    override suspend fun upsertExerciseList(exercises: List<Exercise>) {
        exerciseDao.upsertExerciseList(exercises)
    }

    override suspend fun getAllExercise(): List<Exercise> {
        return exerciseDao.getAllExercise()
    }

    override suspend fun getExerciseById(exerciseId: String): Exercise? {
        return exerciseDao.getExerciseById(exerciseId)
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

    override suspend fun upsertTemplateExerciseRecords(templateExerciseRecordsList: List<TemplateExerciseRecord>) {
        templateExerciseRecordDao.upsertTemplateExerciseRecord(templateExerciseRecordsList)
    }

    override suspend fun upsertTemplateExerciseRecord(templateExerciseRecord: TemplateExerciseRecord) {
        templateExerciseRecordDao.upsertTemplateExerciseRecord(templateExerciseRecord)
    }

    override suspend fun getTemplateExerciseRecordListByTemplateId(templateId: String): List<TemplateExerciseRecord> {
        return templateExerciseRecordDao.getTemplateExerciseRecordListByTemplateId(templateId)
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

    override suspend fun getAllExerciseRecordsByUserId(userId: String): List<ExerciseRecord> {
        return exerciseRecordDao.getExerciseRecordsByUserId(userId)
    }

    override suspend fun deleteYoutubeById(id: String) {
        exerciseYoutubeDao.deleteYoutubeById(id)
    }

    override suspend fun deleteTemplateByTemplateId(templateId: String) {
        templateDao.deleteTemplateByTemplateId(templateId = templateId)
    }

    override suspend fun deleteAllTemplate() {
        templateDao.deleteAllTemplate()
        templateExerciseRecordDao.deleteAllTemplateExerciseRecords()
    }

    override suspend fun test2(): List<ExerciseWithExerciseYoutube>? {
        return try {
            exerciseYoutubeDao.tryRelation("0001")
        } catch (e: Exception) {
            Log.i("gson", "$e")
            null
        }
    }
}