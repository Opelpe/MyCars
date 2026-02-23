package com.pepe.mycars.data.di

import com.pepe.mycars.domain.repository.IAuthRepository
import com.pepe.mycars.domain.repository.IFuelDataRepository
import com.pepe.mycars.domain.usecase.auth.LogOutUseCase
import com.pepe.mycars.domain.usecase.auth.LoginUseCase
import com.pepe.mycars.domain.usecase.auth.RegisterAsGuestUseCase
import com.pepe.mycars.domain.usecase.auth.RegisterUseCase
import com.pepe.mycars.domain.usecase.auth.SignInWithGoogleUseCase
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
    fun provideGetRefillItemsUseCase(fuelDataRepository: IFuelDataRepository): GetRefillItemsUseCase =
        GetRefillItemsUseCase(fuelDataRepository)

    @Provides
    @Singleton
    fun provideDeleteItemUseCase(fuelDataRepository: IFuelDataRepository): DeleteItemUseCase = DeleteItemUseCase(fuelDataRepository)

    @Provides
    @Singleton
    fun provideLogOutUseCase(authRepository: IAuthRepository): LogOutUseCase = LogOutUseCase(authRepository)

    @Provides
    @Singleton
    fun provideLoginUseCase(authRepository: IAuthRepository): LoginUseCase = LoginUseCase(authRepository)

    @Provides
    @Singleton
    fun provideRegisterUseCase(authRepository: IAuthRepository): RegisterUseCase = RegisterUseCase(authRepository)

    @Provides
    @Singleton
    fun provideRegisterAsGuestUseCase(authRepository: IAuthRepository): RegisterAsGuestUseCase = RegisterAsGuestUseCase(authRepository)

    @Provides
    @Singleton
    fun provideSignInWithGoogleUseCase(authRepository: IAuthRepository): SignInWithGoogleUseCase = SignInWithGoogleUseCase(authRepository)
}
