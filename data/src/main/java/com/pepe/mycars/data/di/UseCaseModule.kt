package com.pepe.mycars.data.di

import com.pepe.mycars.domain.repository.IAuthRepository
import com.pepe.mycars.domain.repository.IFuelDataRepository
import com.pepe.mycars.domain.usecase.auth.LogOutUseCase
import com.pepe.mycars.domain.usecase.fuel.DeleteItemUseCase
import com.pepe.mycars.domain.usecase.fuel.GetRefillItemsUseCase
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
    fun provideLogOutUseCase(authRepository: IAuthRepository): LogOutUseCase {
        return LogOutUseCase(authRepository)
    }
}
