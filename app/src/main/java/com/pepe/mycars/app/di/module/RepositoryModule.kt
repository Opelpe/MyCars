package com.pepe.mycars.app.di.module

import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pepe.mycars.app.data.domain.repository.AuthRepository
import com.pepe.mycars.app.data.domain.repository.DataRepository
import com.pepe.mycars.app.data.repository.AuthRepositoryImpl
import com.pepe.mycars.app.data.repository.DataRepositoryImpl
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
        appPreferences: SharedPreferences,
    ): AuthRepository {
        return AuthRepositoryImpl(auth, database, appPreferences)
    }

    @Provides
    @Singleton
    fun provideDataRepository(
        database: FirebaseFirestore,
        auth: FirebaseAuth,
    ): DataRepository {
        return DataRepositoryImpl(database, auth)
    }
}
