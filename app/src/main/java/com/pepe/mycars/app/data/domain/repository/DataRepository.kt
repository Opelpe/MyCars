package com.pepe.mycars.app.data.domain.repository
import com.pepe.mycars.app.utils.state.ItemModelState
import kotlinx.coroutines.flow.Flow

interface DataRepository {

    fun getUserItems(): Flow<ItemModelState>

    fun addDataRefillItem(
        currMileage: String,
        fuelCost: String,
        fuelAmount: String,
        refillDate: String,
        notes: String
    ): Flow<ItemModelState>
}