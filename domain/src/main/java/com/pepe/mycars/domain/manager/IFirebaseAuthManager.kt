package com.pepe.mycars.domain.manager

interface IFirebaseAuthManager {

    fun getCurrentUserId() : String?

    fun signOut()

    suspend fun signInAnonymously(): Result<Unit>

    suspend fun signInWithEmailAndPassword(email: String, password: String): Result<Unit>

    suspend fun createUserWithEmailAndPassword(email: String, password: String): Result<Unit>

    suspend fun signInWithCredential(idToken: String): Result<Unit>
}
