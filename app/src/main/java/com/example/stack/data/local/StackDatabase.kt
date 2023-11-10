package com.example.stack.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import com.example.stack.data.dataclass.Exercise
import com.example.stack.data.dataclass.ExerciseRecord
import com.example.stack.data.dataclass.ExerciseYoutube
import com.example.stack.data.dataclass.RepsAndWeightsConverter
import com.example.stack.data.dataclass.StringListConverter
import com.example.stack.data.dataclass.Template
import com.example.stack.data.dataclass.TemplateExerciseRecord
import com.example.stack.data.dataclass.User
import com.example.stack.data.dataclass.UserInfo
import com.example.stack.data.dataclass.Workout

@TypeConverters(RepsAndWeightsConverter::class, StringListConverter::class)
@Database(entities = [User::class, Exercise::class, ExerciseRecord::class
    , ExerciseYoutube::class, TemplateExerciseRecord::class
    , Template::class, Workout::class, UserInfo::class], version = 1, exportSchema = false)
abstract class StackDatabase : RoomDatabase() {

    abstract val userDao: UserDao
    abstract val exerciseDao: ExerciseDao
    abstract val exerciseRecordDao: ExerciseRecordDao
    abstract val exerciseYoutubeDao: ExerciseYoutubeDao
    abstract val templateExerciseRecordDao: TemplateExerciseRecordDao
    abstract val templateDao: TemplateDao
    abstract val workoutDao: WorkoutDao
    abstract val userInfoDao: UserInfoDao

    companion object {

        @Volatile
        private var INSTANCE: StackDatabase? = null

        fun getInstance(context: Context): StackDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        StackDatabase::class.java,
                        "stylish_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}