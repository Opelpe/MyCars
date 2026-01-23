package com.pepe.mycars.app.di.module

import com.pepe.mycars.app.data.domain.repository.AuthRepository
import com.pepe.mycars.app.data.domain.usecase.auth.LogOutUseCase
import com.pepe.mycars.app.data.domain.usecase.data.DeleteItemUseCase
import com.pepe.mycars.app.data.domain.usecase.data.GetRefillItemsUseCase
import com.pepe.mycars.data.firebase.repo.IFuelDataRepository
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
    fun provideGetUserDataUseCase(fuelDataRepository: IFuelDataRepository): GetRefillItemsUseCase {
        return GetRefillItemsUseCase(fuelDataRepository)
    }

    @Provides
    @Singleton
    fun provideDeleteItemUseCase(fuelDataRepository: IFuelDataRepository): DeleteItemUseCase {
        return DeleteItemUseCase(fuelDataRepository)
    }

    @Provides
    @Singleton
    fun provideLogOutUseCase(authRepository: AuthRepository): LogOutUseCase {
        return LogOutUseCase(authRepository)
    }
}
