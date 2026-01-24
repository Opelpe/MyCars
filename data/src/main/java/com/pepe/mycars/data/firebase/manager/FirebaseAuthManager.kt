package com.pepe.mycars.data.firebase.manager

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.pepe.mycars.domain.manager.IFirebaseAuthManager
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseAuthManager @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
) : IFirebaseAuthManager {

    override fun getCurrentUserId(): String? = firebaseAuth.currentUser?.uid

    override fun signOut() {
        firebaseAuth.signOut()
    }

    override suspend fun createUserWithEmailAndPassword(
        email: String,
        password: String
    ): Result<Unit> {
        firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        return Result.success(Unit)
    }

    override suspend fun signInWithCredential(idToken: String): Result<Unit> {
        val googleCredentials = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(googleCredentials).await()
        return Result.success(Unit)
    }

    override suspend fun signInAnonymously(): Result<Unit> = try {
        firebaseAuth.signInAnonymously().await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun signInWithEmailAndPassword(email: String, password: String): Result<Unit> {
        firebaseAuth.signInWithEmailAndPassword(email, password).await()
        return Result.success(Unit)
    }

}
