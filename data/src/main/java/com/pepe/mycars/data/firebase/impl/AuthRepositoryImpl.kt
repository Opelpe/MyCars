package com.pepe.mycars.data.firebase.impl

import android.content.SharedPreferences
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.pepe.mycars.data.dto.UserDto
import com.pepe.mycars.data.firebase.repo.IAuthRepository
import com.pepe.mycars.domain.model.AccountProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl
    @Inject
    constructor(
        private val firebaseAuth: FirebaseAuth,
        private val fireStoreDatabase: FirebaseFirestore,
        private val sharedPreferences: SharedPreferences,
    ) : IAuthRepository {
        override fun register(
            email: String,
            password: String,
            name: String,
            autoLogin: Boolean,
        ): Flow<FirebaseUser> =
            flow {
                val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
                val user = result.user ?: error("Registration failed: No user returned")

                val providerType = result.credential?.provider ?: AccountProvider.EMAIL.value
                updateUserPreferences(providerType, name, autoLogin, isLoggedIn = true)

                emit(user)
            }

        override fun registerWithGoogle(
            authCredential: AuthCredential,
            name: String,
            email: String,
            autoLogin: Boolean,
        ): Flow<FirebaseUser> =
            flow {
                val result = firebaseAuth.signInWithCredential(authCredential).await()
                val user = result.user ?: error("Google sign-in failed")

                val providerType = result.credential?.provider ?: AccountProvider.GOOGLE.value
                updateUserPreferences(providerType, name, autoLogin, isLoggedIn = true)

                emit(user)
            }

        override fun registerAsGuest(autoLogin: Boolean): Flow<FirebaseUser> =
            flow {
                val result = firebaseAuth.signInAnonymously().await()
                val user = result.user ?: error("Anonymous sign-in failed")

                val providerType = result.credential?.provider ?: AccountProvider.ANONYMOUS.value
                updateUserPreferences(providerType, GUEST_NAME, autoLogin, isLoggedIn = true)

                emit(user)
            }

        override fun login(
            email: String,
            password: String,
            autoLogin: Boolean,
        ): Flow<FirebaseUser?> =
            flow {
                val snapshot =
                    fireStoreDatabase.collection(COLLECTION_USER)
                        .whereEqualTo(FIELD_EMAIL, email)
                        .get()
                        .await()

                val providerType =
                    if (!snapshot.isEmpty) {
                        val userDto = snapshot.documents[0].toObject(UserDto::class.java)
                        val type = userDto?.providerType ?: AccountProvider.EMAIL.value
                        if (type == AccountProvider.GOOGLE.value) {
                            error("Please sign in with Google")
                        }
                        type
                    } else {
                        AccountProvider.EMAIL.value
                    }

                val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
                val user = result.user

                updateUserPreferences(providerType = providerType, isLoggedIn = user != null)

                emit(user)
            }

        override fun logOut() {
            updateUserPreferences(isLoggedIn = false)
            firebaseAuth.signOut()
        }

        override fun getLoggedUser(): Flow<FirebaseUser?> =
            flow {
                val autoLogin = sharedPreferences.getBoolean(PREF_AUTO_LOGIN, false)
                val providerType = sharedPreferences.getString(PREF_PROVIDER, "") ?: ""

                if (!autoLogin && providerType != AccountProvider.ANONYMOUS.value)
                    {
                        logOut()
                    }
                val user = firebaseAuth.currentUser
                val isLoggedIn = user != null && autoLogin
                updateUserPreferences(isLoggedIn = isLoggedIn)
                emit(user)
            }

        private fun updateUserPreferences(
            providerType: String? = null,
            name: String? = null,
            autoLogin: Boolean? = null,
            isLoggedIn: Boolean? = null,
        ) {
            sharedPreferences.edit().apply {
                isLoggedIn?.let { putBoolean(PREF_IS_LOGGED_IN, it) }
                providerType?.let { putString(PREF_PROVIDER, it) }
                name?.let { putString(PREF_USER_NAME, it) }
                autoLogin?.let { putBoolean(PREF_AUTO_LOGIN, it) }
            }.apply()
        }

        companion object {
            private const val COLLECTION_USER = "User"
            private const val FIELD_EMAIL = "email"
            private const val GUEST_NAME = "Guest"

            private const val PREF_IS_LOGGED_IN = "isLoggedIn"
            private const val PREF_PROVIDER = "provider"
            private const val PREF_USER_NAME = "userName"
            private const val PREF_AUTO_LOGIN = "autoLogin"
        }
    }
