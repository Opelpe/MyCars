package com.pepe.mycars.app.data.domain.repository
import com.pepe.mycars.app.utils.state.ItemModelState
import kotlinx.coroutines.flow.Flow

interface DataRepository {

    fun getUserItems(): Flow<ItemModelState>

    fun addRefillItem(
        currMileage: Float,
        fuelCost: Float,
        fuelAmount: Float,
        refillDate: String,
        notes: String
    ): Flow<ItemModelState>

    fun deleteRefillItem(itemId: String): Flow<ItemModelState>
}