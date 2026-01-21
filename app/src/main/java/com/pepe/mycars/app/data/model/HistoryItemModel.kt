package com.pepe.mycars.app.data.model

import com.google.firebase.firestore.DocumentId

data class HistoryItemModel(
    @DocumentId
    val itemId: String? = null,
    val currMileage: Float? = null,
    val fuelPrice: Float? = null,
    val fuelAmount: Float? = null,
    val refillDate: String = "",
    val notes: String = "",
    val fullTank: Boolean = true,
)
