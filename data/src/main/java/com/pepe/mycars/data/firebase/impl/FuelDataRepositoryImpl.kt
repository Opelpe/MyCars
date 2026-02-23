package com.pepe.mycars.data.firebase.impl

import com.pepe.mycars.data.dto.FuelDataDto
import com.pepe.mycars.data.firebase.manager.FirestoreManager
import com.pepe.mycars.domain.model.FuelDataInfo
import com.pepe.mycars.domain.repository.IFuelDataRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class FuelDataRepositoryImpl
    @Inject
    constructor(
        private val firestoreManager: FirestoreManager,
    ) : IFuelDataRepository {
        override fun getUserItems(): Flow<List<FuelDataInfo>> =
            flow {
                emit(
                    firestoreManager
                        .fetchRefills()
                        .map {
                            it.toDomain()
                        },
                )
            }

        override fun observeUserItems(): Flow<List<FuelDataInfo>> =
            firestoreManager.observeRefillsData().map { list ->
                list.map { it.toDomain() }
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
                val itemId = UUID.randomUUID().toString()
                val dto =
                    FuelDataDto(
                        itemId = itemId,
                        currMileage = currMileage,
                        fuelPrice = fuelCost,
                        fuelAmount = fuelAmount,
                        refillDate = refillDate,
                        notes = notes,
                        fullTank = fullTank,
                    )

                firestoreManager.saveRefill(dto, itemId)

                emit(
                    firestoreManager
                        .fetchRefills()
                        .map { it.toDomain() },
                )
            }

        override fun deleteRefillItem(itemId: String): Flow<List<FuelDataInfo>> =
            flow {
                firestoreManager.deleteRefill(itemId)
                emit(
                    firestoreManager
                        .fetchRefills()
                        .map { it.toDomain() },
                )
            }

        override fun getItemById(itemId: String): Flow<FuelDataInfo> =
            flow {
                val response = firestoreManager.getRefillItemById(itemId) ?: error("Item not found")
                emit(response.toDomain())
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

                firestoreManager.saveRefill(refillDto, itemID)

                emit(firestoreManager.fetchRefills().map { it.toDomain() })
            }
    }
