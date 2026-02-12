package com.pepe.mycars.domain.repository

import kotlinx.coroutines.flow.Flow

interface IAuthRepository {
    val validSessionFlow: Flow<Boolean>

    fun register(
        email: String,
        password: String,
        name: String,
    ): Flow<Boolean>

    fun registerWithGoogle(
        idToken: String,
        name: String,
        email: String,
    ): Flow<Boolean>

    fun registerAsGuest(): Flow<Boolean>

    fun login(
        email: String,
        password: String,
    ): Flow<Boolean>

    fun logOut()
}