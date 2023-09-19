package com.example.stack.di

import android.content.Context
import androidx.room.Room
import com.example.stack.data.DefaultRepository
import com.example.stack.data.StackRepository
import com.example.stack.data.local.ExerciseDao
import com.example.stack.data.local.ExerciseRecordDao
import com.example.stack.data.local.ExerciseYoutubeDao
import com.example.stack.data.local.StackDatabase
import com.example.stack.data.local.UserDao
import com.example.stack.data.network.NetworkDataSource
import com.example.stack.data.network.StackNetworkDataSource
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
}