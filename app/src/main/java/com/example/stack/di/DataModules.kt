package com.example.stack.di

import com.example.stack.data.DefaultRepository
import com.example.stack.data.StackRepository
import com.example.stack.data.network.NetworkDataSource
import com.example.stack.data.network.StackNetworkDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
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