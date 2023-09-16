package com.example.stack.data

import com.example.stack.data.StackRepository
import com.example.stack.data.network.NetworkDataSource
import javax.inject.Inject

class DefaultRepository @Inject constructor(
    private val networkDataSource: NetworkDataSource
): StackRepository{
    override fun test2(){
        networkDataSource.test()
    }
}