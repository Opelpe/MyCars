package com.pepe.mycars.data.firebase.impl

import android.content.SharedPreferences
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.FirebaseFirestore
import com.pepe.mycars.data.dto.CommonApiResponse
import com.pepe.mycars.data.dto.UserDto
import com.pepe.mycars.data.firebase.manager.FirebaseAuthManager
import com.pepe.mycars.domain.model.AccountProvider
import com.pepe.mycars.domain.model.UserInfo
import com.pepe.mycars.domain.repository.IUserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.io.IOException
import java.util.Locale
import javax.inject.Inject

class UserRepositoryImpl
    @Inject
    constructor(
        private val fireStoreDatabase: FirebaseFirestore,
        private val sharedPreferences: SharedPreferences,
        private val authManager: FirebaseAuthManager,
    ) : IUserRepository {
        override fun getSyncFirestoreUserData(): Flow<UserInfo?> =
            flow {
                val userId = authManager.firebaseUserId
                if (userId.isNullOrEmpty())
                    {
                        emit(null)
                        return@flow
                    }
                val userRef = fireStoreDatabase.collection(COLLECTION_PATH_USER).document(userId)
                val isAnonymous = authManager.anonymousFirebaseUser == true
                val guestCount =
                    if (isAnonymous) {
                        fireStoreDatabase.collection(COLLECTION_PATH_USER)
                            .whereEqualTo("providerType", AccountProvider.ANONYMOUS.value)
                            .count().get(AggregateSource.SERVER).await().count
                    } else {
                        0L
                    }

                val result =
                    fireStoreDatabase.runTransaction { transaction ->
                        val snapshot = transaction.get(userRef)
                        val autoLogin = sharedPreferences.getBoolean("autoLogin", false)
                        val providerType = sharedPreferences.getString("provider", "") ?: ""

                        if (snapshot.exists()) {
                            transaction.update(userRef, mapOf("autoLogin" to autoLogin, "providerType" to providerType))
                            snapshot.toObject(UserDto::class.java)!!
                                .copy(autoLogin = autoLogin, providerType = providerType)
                                .toDomain()
                        } else {
                            val name =
                                if (isAnonymous) {
                                    "Guest($guestCount)"
                                } else {
                                    sharedPreferences.getString("userName", "") ?: ""
                                }

                            val providerType =
                                if (isAnonymous) {
                                    AccountProvider.ANONYMOUS
                                } else {
                                    AccountProvider.EMAIL
                                }
                            val newUser =
                                UserInfo(
                                    id = userId,
                                    name = name,
                                    email = authManager.firebaseUserEmail ?: "",
                                    active = true,
                                    country = Locale.getDefault().country,
                                    providerType = providerType,
                                    autoLogin = autoLogin,
                                )
                            transaction.set(userRef, UserDto.fromDomain(newUser))
                            newUser
                        }
                    }.await()

                emit(result)
            }

        private fun handleFailure(e: Exception): CommonApiResponse.Error =
            when (e) {
                is IOException -> CommonApiResponse.Error("Check Your Internet Connection")
                else -> CommonApiResponse.Error(e.localizedMessage ?: "An unexpected error occurred")
            }

        override fun getUserProviderType(): String = sharedPreferences.getString("provider", "") ?: ""

        override fun getUserAutoLogin(): Boolean = sharedPreferences.getBoolean("autoLogin", false)

        companion object {
            private const val COLLECTION_PATH_USER = "User"
        }
    }
