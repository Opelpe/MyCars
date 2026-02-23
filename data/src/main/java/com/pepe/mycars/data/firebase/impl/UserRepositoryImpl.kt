package com.pepe.mycars.data.firebase.impl

import android.content.SharedPreferences
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.FirebaseFirestore
import com.pepe.mycars.data.dto.UserDto
import com.pepe.mycars.data.firebase.manager.FirebaseAuthManager
import com.pepe.mycars.data.firebase.manager.FirestoreManager
import com.pepe.mycars.domain.model.AccountProvider
import com.pepe.mycars.domain.model.UserInfo
import com.pepe.mycars.domain.repository.IUserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.util.Locale
import javax.inject.Inject

class UserRepositoryImpl
    @Inject
    constructor(
        private val fireStoreDatabase: FirebaseFirestore,
        private val sharedPreferences: SharedPreferences,
        private val authManager: FirebaseAuthManager,
        private val firestoreManager: FirestoreManager,
    ) : IUserRepository {
        override fun getSyncFirestoreUserData(): Flow<UserInfo?> =
            flow {
                val providerType = sharedPreferences.getString("provider", "")

                firestoreManager.getFirestoreUserData(providerType)

                val userId = authManager.firebaseUserId
                if (userId.isNullOrEmpty()) {
                    emit(null)
                    return@flow
                }
                val userRef =
                    fireStoreDatabase
                        .collection(COLLECTION_PATH_USER)
                        .document(userId)
                val isAnonymous = authManager.anonymousFirebaseUser == true
                val guestCount =
                    if (isAnonymous) {
                        fireStoreDatabase
                            .collection(COLLECTION_PATH_USER)
                            .whereEqualTo("providerType", AccountProvider.ANONYMOUS.value)
                            .count()
                            .get(AggregateSource.SERVER)
                            .await().count
                    } else {
                        0L
                    }

                val result =
                    fireStoreDatabase.runTransaction { transaction ->
                        val snapshot = transaction.get(userRef)
                        val providerType = sharedPreferences.getString("provider", "") ?: ""

                        if (snapshot.exists()) {
                            transaction.update(userRef, mapOf("providerType" to providerType))
                            snapshot.toObject(UserDto::class.java)!!
                                .copy(providerType = providerType)
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
                                )
                            transaction.set(userRef, UserDto.fromDomain(newUser))
                            newUser
                        }
                    }.await()

                emit(result)
            }

        override fun getUserProviderType(): String = sharedPreferences.getString("provider", "") ?: ""

        companion object {
            private const val COLLECTION_PATH_USER = "User"
        }
    }
