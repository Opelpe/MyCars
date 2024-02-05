package com.pepe.mycars.app.di.module

import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pepe.mycars.app.data.domain.repository.AuthRepository
import com.pepe.mycars.app.data.domain.repository.DataRepository
import com.pepe.mycars.app.data.domain.repository.UserRepository
import com.pepe.mycars.app.data.repository.AuthRepositoryImpl
import com.pepe.mycars.app.data.repository.DataRepositoryImpl
import com.pepe.mycars.app.data.repository.UserRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object RepositoryModule {

    @Provides
    @Singleton
    fun provideAuthRepository(
        database: FirebaseFirestore,
        auth: FirebaseAuth,
        appPreferences: SharedPreferences
    ): AuthRepository {
        return AuthRepositoryImpl(auth,database,appPreferences)
    }

    @Provides
    @Singleton
    fun provideUserRepository(
        database: FirebaseFirestore,
        auth: FirebaseAuth,
        appPreferences: SharedPreferences
    ): UserRepository {
        return UserRepositoryImpl(database, auth, appPreferences)
    }

    @Provides
    @Singleton
    fun provideDataRepository(
        database: FirebaseFirestore,
        auth: FirebaseAuth,
        appPreferences: SharedPreferences
    ): DataRepository {
        return DataRepositoryImpl(database, auth, appPreferences)
    }
}