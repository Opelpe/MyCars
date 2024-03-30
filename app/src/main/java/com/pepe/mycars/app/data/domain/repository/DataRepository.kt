package com.pepe.mycars.app.data.domain.repository

import com.google.firebase.firestore.CollectionReference
import com.pepe.mycars.app.utils.state.ItemModelState
import kotlinx.coroutines.flow.Flow

interface DataRepository {

    fun getRefillsCollectionReference(): CollectionReference

    fun getUserItems(): Flow<ItemModelState>

    fun addRefillItem(
        currMileage: Float,
        fuelCost: Float,
        fuelAmount: Float,
        refillDate: String,
        notes: String,
        fullTank: Boolean
    ): Flow<ItemModelState>

    fun deleteRefillItem(itemId: String): Flow<ItemModelState>

    fun getItemById(itemId: String): Flow<ItemModelState>

    fun updateItem(
        itemID: String,
        currMileage: Float,
        fuelAmount: Float,
        fuelCost: Float,
        refillDate: String,
        notes: String,
        fullTank: Boolean
    ): Flow<ItemModelState>
}