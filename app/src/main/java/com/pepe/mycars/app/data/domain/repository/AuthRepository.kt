package com.pepe.mycars.app.data.domain.repository

import com.google.firebase.auth.AuthCredential
import com.pepe.mycars.app.utils.state.AuthState
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    fun register(email: String, password: String, name: String, autoLogin: Boolean): Flow<AuthState>

    fun registerWithGoogle(authCredential: AuthCredential, name: String, email: String, autoLogin: Boolean): Flow<AuthState>

    fun registerAsGuest(autoLogin: Boolean):Flow<AuthState>

    fun login(email: String, password: String, autoLogin: Boolean): Flow<AuthState>

    fun logOut()

    fun getLoggedUser(): Flow<AuthState>
}