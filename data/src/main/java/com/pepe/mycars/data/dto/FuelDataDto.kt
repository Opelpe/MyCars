package com.pepe.mycars.data.dto

import com.google.firebase.firestore.DocumentId
import com.pepe.mycars.domain.model.FuelDataInfo

data class FuelDataDto(
    @DocumentId
    val itemId: String? = null,
    val currMileage: Float? = null,
    val fuelPrice: Float? = null,
    val fuelAmount: Float? = null,
    val refillDate: String? = null,
    val notes: String? = null,
    val fullTank: Boolean? = null,
) {
    fun toDomain(): FuelDataInfo {
        return FuelDataInfo(
            itemId = itemId ?: "",
            currMileage = currMileage ?: 0f,
            fuelPrice = fuelPrice ?: 0f,
            fuelAmount = fuelAmount ?: 0f,
            refillDate = refillDate ?: "",
            notes = notes ?: "",
            fullTank = fullTank ?: true,
        )
    }
}
