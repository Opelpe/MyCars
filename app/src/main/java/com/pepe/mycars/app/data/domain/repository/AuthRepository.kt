package com.pepe.mycars.app.data.domain.repository

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser
import com.pepe.mycars.app.utils.Resource
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    fun register(email: String, password: String, name: String, autoLogin: Boolean): Flow<Resource<FirebaseUser>>

    fun registerWithGoogle(authCredential: AuthCredential, name: String, email: String, autoLogin: Boolean): Flow<Resource<FirebaseUser>>

    fun registerAsGuest(autoLogin: Boolean):Flow<Resource<FirebaseUser>>

    fun login(email: String?, password: String?, autoLogin: Boolean): Flow<Resource<FirebaseUser>>

    fun logOut()

    fun getLoggedUser(): Flow<Resource<FirebaseUser>>
}