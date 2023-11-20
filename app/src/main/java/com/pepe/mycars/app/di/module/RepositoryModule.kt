package com.pepe.mycars.app.di.module

import com.pepe.mycars.app.data.domain.repository.MyRepository
import com.pepe.mycars.app.data.domain.repository.UserRepository
import com.pepe.mycars.app.data.repository.MyRepositoryImpl
import com.pepe.mycars.app.data.repository.UserRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindMyRepository(
        myRepositoryImpl: MyRepositoryImpl
    ): MyRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository
}