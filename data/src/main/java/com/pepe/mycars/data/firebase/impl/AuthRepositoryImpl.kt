package com.pepe.mycars.data.firebase.impl

import android.content.SharedPreferences
import androidx.core.content.edit
import com.pepe.mycars.data.firebase.manager.FirebaseAuthManager
import com.pepe.mycars.domain.model.AccountProvider
import com.pepe.mycars.domain.repository.IAuthRepository
import com.pepe.mycars.domain.repository.IUserRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

class AuthRepositoryImpl
    @Inject
    constructor(
        private val sharedPreferences: SharedPreferences,
        private val authManager: FirebaseAuthManager,
        private val userRepository: IUserRepository,
    ) : IAuthRepository {
        override val validSessionFlow: Flow<Boolean> =
            callbackFlow {
                val job =
                    launch {
                        authManager.authStateFlow.collectLatest { isAuthenticated ->
                            updateUserPreferences(isLoggedIn = isAuthenticated)
                            trySend(isAuthenticated)
                        }
                    }
                awaitClose { job.cancel() }
            }

        override fun register(
            email: String,
            password: String,
            name: String,
            autoLogin: Boolean,
        ): Flow<Boolean> =
            flow {
                val result = authManager.createUserWithEmailAndPassword(email, password)
                if (result.isSuccess) {
                    updateUserPreferences(
                        providerType = AccountProvider.EMAIL.value,
                        name = GUEST_NAME,
                        isLoggedIn = true,
                    )

                    emit(true)
                } else {
                    error("Registration failed")
                }
            }

        override fun registerWithGoogle(
            idToken: String,
            name: String,
            email: String,
        ) = flow {
            val result = authManager.signInWithCredential(idToken)
            if (result.isSuccess) {
                updateUserPreferences(
                    providerType = AccountProvider.GOOGLE.value,
                    name = name,
                    isLoggedIn = true,
                )
                emit(true)
            } else {
                error("Google Sign In Failed")
            }
        }

        override fun registerAsGuest() =
            flow {
                val result = authManager.signInAnonymously()
                if (result.isSuccess) {
                    updateUserPreferences(
                        providerType = AccountProvider.ANONYMOUS.value,
                        name = GUEST_NAME,
                        isLoggedIn = true,
                    )
                    emit(true)
                } else {
                    error("Anonymous Sign In failed")
                }
            }

        override fun login(
            email: String,
            password: String,
            autoLogin: Boolean,
        ): Flow<Boolean> =
            flow {
                val authResult = authManager.signInWithEmailAndPassword(email, password)

                if (authResult.isSuccess) {
                    userRepository.getSyncFirestoreUserData().collect { user ->
                        if (user == null) error("User data not found")

                        if (user.providerType == AccountProvider.GOOGLE) {
                            authManager.signOut()
                            error("This account is registered with Google. Please sign in with Google.")
                        }

                        updateUserPreferences(
                            providerType = AccountProvider.EMAIL.value,
                            name = user.name,
                            autoLogin = autoLogin,
                            isLoggedIn = true,
                        )

                        emit(true)
                    }
                } else {
                    error("Login failed")
                }
            }

        override fun logOut() {
            authManager.signOut()
            updateUserPreferences(isLoggedIn = false)
        }

        private fun updateUserPreferences(
            providerType: String? = null,
            name: String? = null,
            autoLogin: Boolean? = null,
            isLoggedIn: Boolean? = null,
        ) {
            sharedPreferences.edit {
                isLoggedIn?.let { putBoolean(PREF_IS_LOGGED_IN, it) }
                providerType?.let { putString(PREF_PROVIDER, it) }
                name?.let { putString(PREF_USER_NAME, it) }
                autoLogin?.let { putBoolean(PREF_AUTO_LOGIN, it) }
            }
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
