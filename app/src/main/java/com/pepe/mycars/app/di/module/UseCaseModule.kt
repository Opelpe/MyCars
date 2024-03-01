package com.pepe.mycars.app.di.module

import com.pepe.mycars.app.data.domain.repository.DataRepository
import com.pepe.mycars.app.data.domain.usecase.data.AddItemUseCase
import com.pepe.mycars.app.data.domain.usecase.data.DeleteItemUseCase
import com.pepe.mycars.app.data.domain.usecase.data.GetUserDataUseCase
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
    fun provideGetUserDataUseCase(
        dataRepository: DataRepository
    ): GetUserDataUseCase {
        return GetUserDataUseCase(dataRepository)
    }

    @Provides
    @Singleton
    fun provideAddItemUseCase(
        dataRepository: DataRepository
    ): AddItemUseCase {
        return AddItemUseCase(dataRepository)
    }

    @Provides
    @Singleton
    fun provideDeleteItemUseCase(
        dataRepository: DataRepository
    ): DeleteItemUseCase {
        return DeleteItemUseCase(dataRepository)
    }
}