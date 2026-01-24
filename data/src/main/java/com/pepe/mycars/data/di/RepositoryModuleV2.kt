package com.pepe.mycars.data.di

import com.pepe.mycars.data.firebase.impl.AuthRepositoryImpl
import com.pepe.mycars.data.firebase.impl.FuelDataRepositoryImpl
import com.pepe.mycars.data.firebase.impl.UserRepositoryImpl
import com.pepe.mycars.data.firebase.repo.IAuthRepository
import com.pepe.mycars.domain.repository.IFuelDataRepository
import com.pepe.mycars.data.firebase.repo.IUserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModuleV2 {
    @Binds
    abstract fun bindUserRepository(impl: UserRepositoryImpl): IUserRepository

    @Binds
    abstract fun bindFuelDataRepository(impl: FuelDataRepositoryImpl): IFuelDataRepository

    @Binds
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): IAuthRepository
}
