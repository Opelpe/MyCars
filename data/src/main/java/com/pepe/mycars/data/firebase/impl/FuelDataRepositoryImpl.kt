package com.pepe.mycars.data.firebase.impl

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import com.pepe.mycars.data.dto.FuelDataDto
import com.pepe.mycars.data.firebase.manager.FirebaseAuthManager
import com.pepe.mycars.domain.model.FuelDataInfo
import com.pepe.mycars.domain.repository.IFuelDataRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FuelDataRepositoryImpl
    @Inject
    constructor(
        private val fireStoreDatabase: FirebaseFirestore,
        private val authManager: FirebaseAuthManager,
    ) : IFuelDataRepository {
        private fun getRefillsRef(): CollectionReference {
            val uId = authManager.firebaseUserId ?: error(MESSAGE_NOT_LOGGED)
            return fireStoreDatabase
                .collection(COLLECTION_PATH_USER)
                .document(uId)
                .collection(COLLECTION_PATH_REFILLS)
        }

        private suspend fun fetchRefillsList(): List<FuelDataInfo> =
            getRefillsRef()
                .orderBy(FIELD_CURR_MILEAGE, Query.Direction.DESCENDING)
                .get()
                .await()
                .toObjects(FuelDataDto::class.java)
                .map { it.toDomain() }

        override fun getUserItems(): Flow<List<FuelDataInfo>> =
            flow {
                emit(fetchRefillsList())
            }

        override fun observeUserItems(): Flow<List<FuelDataInfo>> {
            return getRefillsRef()
                .orderBy(FIELD_CURR_MILEAGE, Query.Direction.DESCENDING)
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(FuelDataDto::class.java).map { it.toDomain() }
                }
        }

        override fun addRefillItem(
            currMileage: Float,
            fuelCost: Float,
            fuelAmount: Float,
            refillDate: String,
            notes: String,
            fullTank: Boolean,
        ): Flow<List<FuelDataInfo>> =
            flow {
                val refillsRef = getRefillsRef()
                val itemId = refillsRef.document().id
                val refillDto =
                    FuelDataDto(
                        itemId = itemId,
                        currMileage = currMileage,
                        fuelPrice = fuelCost,
                        fuelAmount = fuelAmount,
                        refillDate = refillDate,
                        notes = notes,
                        fullTank = fullTank,
                    )

                refillsRef.document(itemId).set(refillDto).await()
                emit(fetchRefillsList())
            }

        override fun deleteRefillItem(itemId: String): Flow<List<FuelDataInfo>> =
            flow {
                getRefillsRef().document(itemId).delete().await()
                emit(fetchRefillsList())
            }

        override fun getItemById(itemId: String): Flow<FuelDataInfo> =
            flow {
                val snapshot = getRefillsRef().document(itemId).get().await()
                emit(snapshot.toObject(FuelDataDto::class.java)?.toDomain() ?: error(MESSAGE_UNKNOWN_ERROR))
            }

        override fun updateItem(
            itemID: String,
            currMileage: Float,
            fuelAmount: Float,
            fuelCost: Float,
            refillDate: String,
            notes: String,
            fullTank: Boolean,
        ): Flow<List<FuelDataInfo>> =
            flow {
                val refillDto =
                    FuelDataDto(
                        itemId = itemID,
                        currMileage = currMileage,
                        fuelPrice = fuelCost,
                        fuelAmount = fuelAmount,
                        refillDate = refillDate,
                        notes = notes,
                        fullTank = fullTank,
                    )
                getRefillsRef().document(itemID).set(refillDto).await()
                emit(fetchRefillsList())
            }

        companion object {
            private const val COLLECTION_PATH_USER = "User"
            private const val COLLECTION_PATH_REFILLS = "Refills"
            private const val FIELD_CURR_MILEAGE = "currMileage"
            const val MESSAGE_UNKNOWN_ERROR = "Unknown Error"
            const val MESSAGE_NOT_LOGGED = "User not logged in"
        }
    }
