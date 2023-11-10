package com.example.stack.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.stack.data.dataclass.ExerciseWithExerciseYoutube
import com.example.stack.data.dataclass.ExerciseYoutube

@Dao
interface ExerciseYoutubeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertYoutube(youtube: List<ExerciseYoutube>)

    @Update
    suspend fun updateYoutube(youtube: ExerciseYoutube)

    @Query("SELECT * FROM exercise_youtube_table WHERE exercise_id = :exerciseId")
    suspend fun getYoutubeByExerciseId(exerciseId: String): List<ExerciseYoutube>

    @Query("SELECT * FROM exercise_youtube_table WHERE youtube_id = :youtubeId")
    suspend fun getYoutubeByYoutubeId(youtubeId: String): ExerciseYoutube

    @Query("DELETE FROM exercise_youtube_table WHERE youtube_id = :youtubeId")
    suspend fun deleteYoutubeById(youtubeId: String)

    @Query("SELECT a.exercise_id, exercise_name, youtube_title  FROM exercise_youtube_table AS a, exercise_table AS b WHERE a.exercise_id = b.exercise_id AND a.exercise_id = :exerciseId")
    suspend fun tryRelation(exerciseId: String): List<ExerciseWithExerciseYoutube>
}