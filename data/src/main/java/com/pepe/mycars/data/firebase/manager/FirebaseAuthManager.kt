package com.pepe.mycars.data.firebase.manager

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseAuthManager
    @Inject
    constructor(
        private val firebaseAuth: FirebaseAuth,
    ) {
        val firebaseUserId: String?
            get() = firebaseAuth.currentUser?.uid

        val firebaseUserEmail: String?
            get() = firebaseAuth.currentUser?.email

        val anonymousFirebaseUser: Boolean?
            get() = firebaseAuth.currentUser?.isAnonymous

        val authStateFlow: Flow<Boolean> =
            callbackFlow {
                val listener =
                    FirebaseAuth.AuthStateListener { auth ->
                        trySend(auth.currentUser != null)
                    }
                firebaseAuth.addAuthStateListener(listener)
                awaitClose { firebaseAuth.removeAuthStateListener(listener) }
            }

        fun signOut() {
            firebaseAuth.signOut()
        }

        suspend fun createUserWithEmailAndPassword(
            email: String,
            password: String,
        ): Result<FirebaseUser?> {
            try {
                val result =
                    firebaseAuth
                        .createUserWithEmailAndPassword(
                            email,
                            password,
                        )
                        .await()
                return Result.success(result.user)
            } catch (e: Exception) {
                return Result.failure(e)
            }
        }

        suspend fun signInWithCredential(idToken: String): Result<FirebaseUser?> {
            try {
                val googleCredentials = GoogleAuthProvider.getCredential(idToken, null)
                val result =
                    firebaseAuth
                        .signInWithCredential(googleCredentials)
                        .await()
                return Result.success(result.user)
            } catch (e: Exception) {
                return Result.failure(e)
            }
        }

        suspend fun signInAnonymously(): Result<FirebaseUser?> =
            try {
                val result =
                    firebaseAuth
                        .signInAnonymously()
                        .await()
                Result.success(result.user)
            } catch (e: Exception) {
                Result.failure(e)
            }

        suspend fun signInWithEmailAndPassword(
            email: String,
            password: String,
        ): Result<FirebaseUser?> {
            try {
                val result =
                    firebaseAuth
                        .signInWithEmailAndPassword(
                            email,
                            password,
                        )
                        .await()
                return Result.success(result.user)
            } catch (e: Exception) {
                return Result.failure(e)
            }
        }
    }
