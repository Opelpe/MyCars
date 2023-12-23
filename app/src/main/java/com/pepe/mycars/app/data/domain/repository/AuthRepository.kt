package com.pepe.mycars.app.data.domain.repository

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser
import com.pepe.mycars.app.utils.Resource
import com.pepe.mycars.app.utils.ResourceOld
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    fun register(email: String, password: String, name: String, autoLogin: Boolean): Flow<Resource>

    fun registerWithGoogle(authCredential: AuthCredential, name: String, email: String, autoLogin: Boolean): Flow<ResourceOld<FirebaseUser>>

    fun registerAsGuest(autoLogin: Boolean):Flow<ResourceOld<FirebaseUser>>

    fun login(email: String?, password: String?, autoLogin: Boolean): Flow<ResourceOld<FirebaseUser>>

    fun logOut()

    fun getLoggedUser(): Flow<ResourceOld<FirebaseUser>>
}