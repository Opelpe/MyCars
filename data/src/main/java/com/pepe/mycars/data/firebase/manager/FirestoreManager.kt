package com.pepe.mycars.data.firebase.manager

import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import com.pepe.mycars.data.dto.FuelDataDto
import com.pepe.mycars.data.dto.UserDto
import com.pepe.mycars.domain.model.AccountProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.util.Locale
import javax.inject.Inject

class FirestoreManager
    @Inject
    constructor(
        private val firestore: FirebaseFirestore,
        private val authManager: FirebaseAuthManager,
    ) {
        private fun getRefillsRef(): CollectionReference {
            val uId = authManager.firebaseUserId ?: error(MESSAGE_NOT_LOGGED)
            return firestore
                .collection(COLLECTION_PATH_USER)
                .document(uId)
                .collection(COLLECTION_PATH_REFILLS)
        }

        fun observeRefillsData(): Flow<List<FuelDataDto>> =
            getRefillsRef()
                .orderBy(FIELD_CURR_MILEAGE, Query.Direction.DESCENDING)
                .snapshots()
                .map { it.toObjects(FuelDataDto::class.java) }

        suspend fun fetchRefills(): List<FuelDataDto> =
            getRefillsRef()
                .orderBy(FIELD_CURR_MILEAGE, Query.Direction.DESCENDING)
                .get()
                .await()
                .toObjects(FuelDataDto::class.java)

        suspend fun saveRefill(
            dto: FuelDataDto,
            itemId: String,
        ) {
            getRefillsRef().document(itemId).set(dto).await()
        }

        suspend fun deleteRefill(itemId: String) {
            getRefillsRef().document(itemId).delete().await()
        }

        suspend fun getRefillItemById(itemId: String): FuelDataDto? =
            getRefillsRef()
                .document(itemId)
                .get()
                .await()
                .toObject(FuelDataDto::class.java)

        suspend fun getFirestoreUserData(providerType: String?): UserDto? {
            val userId = authManager.firebaseUserId ?: return null
            val userRef = firestore.collection(COLLECTION_PATH_USER).document(userId)

            val isAnonymous = authManager.anonymousFirebaseUser == true
            val guestCount =
                if (isAnonymous) {
                    firestore.collection(COLLECTION_PATH_USER)
                        .whereEqualTo("providerType", AccountProvider.ANONYMOUS.value)
                        .count().get(AggregateSource.SERVER).await().count
                } else {
                    0L
                }

            return firestore.runTransaction { transaction ->
                val snapshot = transaction.get(userRef)

                if (snapshot.exists()) {
                    transaction.update(
                        userRef,
                        mapOf(
                            "providerType" to providerType,
                        ),
                    )
                    snapshot.toObject(UserDto::class.java)!!.copy(
                        providerType = providerType,
                    )
                } else {
                    val name =
                        if (isAnonymous) {
                            "Guest($guestCount)"
                        } else {
                            authManager.firebaseUserEmail
                        }
                    val type = if (isAnonymous) AccountProvider.ANONYMOUS.value else providerType

                    val newUserDto =
                        UserDto(
                            id = userId,
                            name = name,
                            email = authManager.firebaseUserEmail ?: "",
                            active = true,
                            country = Locale.getDefault().country,
                            providerType = type,
                        )
                    transaction.set(userRef, newUserDto)
                    newUserDto
                }
            }.await()
        }

        companion object {
            private const val COLLECTION_PATH_USER = "User"
            private const val COLLECTION_PATH_REFILLS = "Refills"
            private const val FIELD_CURR_MILEAGE = "currMileage"
            const val MESSAGE_UNKNOWN_ERROR = "Unknown Error"
            const val MESSAGE_NOT_LOGGED = "User not logged in"
        }
    }
