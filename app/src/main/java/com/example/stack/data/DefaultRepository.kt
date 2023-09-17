package com.example.stack.data

import com.example.stack.data.StackRepository
import com.example.stack.data.dataclass.User
import com.example.stack.data.local.UserDao
import com.example.stack.data.network.NetworkDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DefaultRepository @Inject constructor(
    private val networkDataSource: NetworkDataSource,
    private val userDao: UserDao
): StackRepository{
    override fun test2(){
        networkDataSource.test()
    }

    override suspend fun upsertUser(user: User) {
        withContext(Dispatchers.IO){
            userDao.upsertUser(user)
        }
    }
}