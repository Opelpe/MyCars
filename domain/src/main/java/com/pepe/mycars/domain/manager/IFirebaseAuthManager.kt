package com.pepe.mycars.domain.manager

import kotlinx.coroutines.flow.Flow

interface IFirebaseAuthManager {

    val firebaseUserId: String?
    val firebaseUserEmail: String?
    val anonymousFirebaseUser: Boolean?
    val authStateFlow: Flow<Boolean>

    fun getCurrentUserId() : String?

    fun signOut()

    suspend fun signInAnonymously(): Result<Unit>

    suspend fun signInWithEmailAndPassword(email: String, password: String): Result<Unit>

    suspend fun createUserWithEmailAndPassword(email: String, password: String): Result<Unit>

    suspend fun signInWithCredential(idToken: String): Result<Unit>
}
