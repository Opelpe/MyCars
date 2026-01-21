package com.pepe.mycars.app.data.local

data class HistoryItemUiModel(
    val itemId: String = "",
    val currMileage: String = "",
    val fuelCost: String = "",
    val fuelAmount: String = "",
    val refillDate: String = "",
    val notes: String = "",
    var addedMileage: String = "",
    var fuelUsage: String = "",
)
