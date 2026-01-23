package com.pepe.mycars.data.firebase.repo

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface IAuthRepository {
    fun register(
        email: String,
        password: String,
        name: String,
        autoLogin: Boolean,
    ): Flow<FirebaseUser>

    fun registerWithGoogle(
        authCredential: AuthCredential,
        name: String,
        email: String,
        autoLogin: Boolean,
    ): Flow<FirebaseUser>

    fun registerAsGuest(autoLogin: Boolean): Flow<FirebaseUser>

    fun login(
        email: String,
        password: String,
        autoLogin: Boolean,
    ): Flow<FirebaseUser?>

    fun logOut()

    fun getLoggedUser(): Flow<FirebaseUser?>
}
