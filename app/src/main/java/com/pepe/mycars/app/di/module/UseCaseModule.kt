package com.pepe.mycars.app.di.module

import com.pepe.mycars.app.data.domain.repository.AuthRepository
import com.pepe.mycars.app.data.domain.repository.DataRepository
import com.pepe.mycars.app.data.domain.usecase.auth.LogOutUseCase
import com.pepe.mycars.app.data.domain.usecase.data.DeleteItemUseCase
import com.pepe.mycars.app.data.domain.usecase.data.GetRefillItemsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    @Provides
    @Singleton
    fun provideGetUserDataUseCase(dataRepository: DataRepository): GetRefillItemsUseCase {
        return GetRefillItemsUseCase(dataRepository)
    }

    @Provides
    @Singleton
    fun provideDeleteItemUseCase(dataRepository: DataRepository): DeleteItemUseCase {
        return DeleteItemUseCase(dataRepository)
    }

    @Provides
    @Singleton
    fun provideLogOutUseCase(authRepository: AuthRepository): LogOutUseCase {
        return LogOutUseCase(authRepository)
    }
}
