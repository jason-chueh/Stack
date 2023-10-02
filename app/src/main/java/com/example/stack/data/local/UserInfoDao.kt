package com.example.stack.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.stack.data.dataclass.UserInfo

@Dao
interface UserInfoDao {
    @Upsert
    fun upsertUserInfo(userInfo: UserInfo)

    @Query("SELECT * FROM user_info WHERE user_id == :userId")
    fun findUserInfoByUserId(userId: String):UserInfo
}