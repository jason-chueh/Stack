package com.example.stack.di

import android.content.Context
import androidx.room.Room
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.example.stack.data.DefaultRepository
import com.example.stack.data.StackRepository
import com.example.stack.data.local.ExerciseDao
import com.example.stack.data.local.ExerciseRecordDao
import com.example.stack.data.local.ExerciseYoutubeDao
import com.example.stack.data.local.StackDatabase
import com.example.stack.data.local.TemplateDao
import com.example.stack.data.local.TemplateExerciseRecordDao
import com.example.stack.data.local.UserDao
import com.example.stack.data.local.UserInfoDao
import com.example.stack.data.local.WorkoutDao
import com.example.stack.data.network.ChatGptApiService
import com.example.stack.data.network.DistanceMatrixService
import com.example.stack.data.network.ExerciseApiService
import com.example.stack.data.network.NetworkDataSource
import com.example.stack.data.network.StackNetworkDataSource
import com.example.stack.data.network.retrofitExercise
import com.example.stack.data.network.retrofitGoogleMap
import com.example.stack.data.network.retrofitGpt
import com.example.stack.login.UserManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {
    @Singleton
    @Binds
    abstract fun bindNetworkDataSource(dataSource: StackNetworkDataSource): NetworkDataSource
}

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {
    @Singleton
    @Provides
    fun provideExerciseApi(): ExerciseApiService{
        return retrofitExercise.create(ExerciseApiService::class.java)
    }
    @Singleton
    @Provides
    fun provideGptApi(): ChatGptApiService {
        return retrofitGpt.create(ChatGptApiService::class.java)
    }

    @Singleton
    @Provides
    fun provideMapApi(): DistanceMatrixService {
        return retrofitGoogleMap.create(DistanceMatrixService::class.java)
    }
}

@Module
@InstallIn(SingletonComponent::class)
object UserManagerModule {

    @Provides
    @Singleton
    fun provideUserManager(): UserManager {
        return UserManager()
    }
}
@Module
@InstallIn(SingletonComponent::class)
object PythonModule {
    @Provides
    @Singleton
    fun providePython(@ApplicationContext context: Context): Python {
        Python.start(AndroidPlatform(context))
        return Python.getInstance()
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Singleton
    @Binds
    abstract fun bindTaskRepository(repository: DefaultRepository): StackRepository
}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDataBase(@ApplicationContext context: Context): StackDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            StackDatabase::class.java,
            "Stack.db"
        ).build()
    }

    @Provides
    fun provideUserDao(database: StackDatabase): UserDao = database.userDao

    @Provides
    fun provideExerciseDao(database: StackDatabase): ExerciseDao = database.exerciseDao

    @Provides
    fun provideExerciseRecordDao(database: StackDatabase): ExerciseRecordDao = database.exerciseRecordDao

    @Provides
    fun provideExerciseYoutubeDao(database: StackDatabase): ExerciseYoutubeDao = database.exerciseYoutubeDao

    @Provides
    fun provideTemplateDao(database: StackDatabase): TemplateDao = database.templateDao

    @Provides
    fun provideTemplateExerciseRecordDao(database: StackDatabase): TemplateExerciseRecordDao = database.templateExerciseRecordDao
    @Provides
    fun provideWorkoutDao(database: StackDatabase): WorkoutDao = database.workoutDao

    @Provides
    fun provideUserInfoDao(database: StackDatabase): UserInfoDao = database.userInfoDao
}