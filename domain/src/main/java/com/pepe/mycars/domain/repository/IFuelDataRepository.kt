package com.pepe.mycars.domain.repository

import com.pepe.mycars.domain.model.FuelDataInfo
import kotlinx.coroutines.flow.Flow

interface IFuelDataRepository {

    fun getUserItems(): Flow<List<FuelDataInfo>>

    fun observeUserItems(): Flow<List<FuelDataInfo>>

    fun addRefillItem(
        currMileage: Float,
        fuelCost: Float,
        fuelAmount: Float,
        refillDate: String,
        notes: String,
        fullTank: Boolean,
    ): Flow<List<FuelDataInfo>>

    fun deleteRefillItem(itemId: String): Flow<List<FuelDataInfo>>

    fun getItemById(itemId: String): Flow<FuelDataInfo>

    fun updateItem(
        itemID: String,
        currMileage: Float,
        fuelAmount: Float,
        fuelCost: Float,
        refillDate: String,
        notes: String,
        fullTank: Boolean,
    ): Flow<List<FuelDataInfo>>
}
