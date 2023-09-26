package com.example.stack.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.example.stack.data.dataclass.User

@Dao
interface UserDao {

    @Upsert
    fun upsertUser(user: User)

    @Query("SELECT * FROM user_table")
    fun getUsers(): LiveData<List<User>>

}