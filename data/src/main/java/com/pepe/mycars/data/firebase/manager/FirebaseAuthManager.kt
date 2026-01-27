package com.pepe.mycars.data.firebase.manager

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.pepe.mycars.domain.manager.IFirebaseAuthManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseAuthManager
    @Inject
    constructor(
        private val firebaseAuth: FirebaseAuth,
    ) : IFirebaseAuthManager {
        override val firebaseUserId: String?
            get() = firebaseAuth.currentUser?.uid

        override val firebaseUserEmail: String?
            get() = firebaseAuth.currentUser?.email

        override val anonymousFirebaseUser: Boolean?
            get() = firebaseAuth.currentUser?.isAnonymous

        override val authStateFlow: Flow<Boolean> =
            callbackFlow {
                val listener =
                    FirebaseAuth.AuthStateListener { auth ->
                        trySend(auth.currentUser != null)
                    }
                firebaseAuth.addAuthStateListener(listener)
                awaitClose { firebaseAuth.removeAuthStateListener(listener) }
            }

        override fun getCurrentUserId(): String? = firebaseAuth.currentUser?.uid

        override fun signOut() {
            firebaseAuth.signOut()
        }

        override suspend fun createUserWithEmailAndPassword(
            email: String,
            password: String,
        ): Result<Unit> {
            try {
                firebaseAuth.createUserWithEmailAndPassword(email, password).await()
                return Result.success(Unit)
            } catch (e: Exception) {
                return Result.failure(e)
            }
        }

        override suspend fun signInWithCredential(idToken: String): Result<Unit> {
            try {
                val googleCredentials = GoogleAuthProvider.getCredential(idToken, null)
                firebaseAuth.signInWithCredential(googleCredentials).await()
                return Result.success(Unit)
            } catch (e: Exception) {
                return Result.failure(e)
            }
        }

        override suspend fun signInAnonymously(): Result<Unit> =
            try {
                firebaseAuth.signInAnonymously().await()
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }

        override suspend fun signInWithEmailAndPassword(
            email: String,
            password: String,
        ): Result<Unit> {
            try {
                firebaseAuth.signInWithEmailAndPassword(email, password).await()
                return Result.success(Unit)
            } catch (e: Exception) {
                return Result.failure(e)
            }
        }
    }
