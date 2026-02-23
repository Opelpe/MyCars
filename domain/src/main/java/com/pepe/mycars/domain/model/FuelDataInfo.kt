package com.pepe.mycars.domain.model

data class FuelDataInfo(
    val itemId: String,
    val currMileage: Float,
    val fuelPrice: Float,
    val fuelAmount: Float,
    val refillDate: String,
    val notes: String,
    val fullTank: Boolean,
)
