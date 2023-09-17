package com.example.stack.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update
import androidx.room.Upsert
import com.example.stack.data.dataclass.User

@Dao
interface UserDao {

    @Upsert
    fun upsertUser(user: User)
}