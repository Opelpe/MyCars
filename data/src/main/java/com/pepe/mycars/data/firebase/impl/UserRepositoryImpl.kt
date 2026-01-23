package com.pepe.mycars.data.firebase.impl

import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.FirebaseFirestore
import com.pepe.mycars.data.dto.CommonApiResponse
import com.pepe.mycars.data.dto.UserDto
import com.pepe.mycars.data.firebase.repo.IUserRepository
import com.pepe.mycars.domain.model.AccountProvider
import com.pepe.mycars.domain.model.UserInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.io.IOException
import java.util.Locale
import javax.inject.Inject

class UserRepositoryImpl
    @Inject
    constructor(
        private val auth: FirebaseAuth,
        private val fireStoreDatabase: FirebaseFirestore,
        private val sharedPreferences: SharedPreferences,
    ) : IUserRepository {
        override fun getLoggedUserData(): Flow<CommonApiResponse<UserInfo>> =
            flow {
                emit(CommonApiResponse.Loading)

                val firebaseUser =
                    auth.currentUser
                        ?: return@flow emit(CommonApiResponse.Error("Not logged"))

                try {
                    val userInfo = syncUserDataWithFirestore(firebaseUser)
                    emit(CommonApiResponse.Success(userInfo))
                } catch (e: Exception) {
                    emit(handleFailure(e))
                }
            }

        private suspend fun syncUserDataWithFirestore(firebaseUser: FirebaseUser): UserInfo {
            val uId = firebaseUser.uid
            val userRef = fireStoreDatabase.collection(COLLECTION_PATH_USER).document(uId)

            val guestCount =
                if (firebaseUser.isAnonymous) {
                    fireStoreDatabase.collection(COLLECTION_PATH_USER)
                        .whereEqualTo("providerType", AccountProvider.ANONYMOUS.value)
                        .count().get(AggregateSource.SERVER).await().count
                } else {
                    0L
                }

            return fireStoreDatabase.runTransaction { transaction ->
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
                        if (firebaseUser.isAnonymous) {
                            "Guest($guestCount)"
                        } else {
                            sharedPreferences.getString("userName", "") ?: ""
                        }

                    val providerType =
                        if (firebaseUser.isAnonymous) {
                            AccountProvider.ANONYMOUS
                        } else {
                            AccountProvider.EMAIL
                        }
                    val newUser =
                        UserInfo(
                            id = uId,
                            name = name,
                            email = firebaseUser.email ?: "",
                            active = true,
                            country = Locale.getDefault().country,
                            providerType = providerType,
                            autoLogin = autoLogin,
                        )
                    transaction.set(userRef, UserDto.fromDomain(newUser))
                    newUser
                }
            }.await()
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
