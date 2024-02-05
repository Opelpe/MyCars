package com.pepe.mycars.app.data.model

import com.google.firebase.firestore.DocumentId

data class ItemRefillModel(
    @DocumentId
    val itemId: String? = null,
    val currMileage: String = "",
    val fuelCost: String = "",
    val fuelAmount: String = "",
    val refillDate: String = "",
    val notes: String = ""
)
