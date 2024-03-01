package com.pepe.mycars.app.data.domain.usecase.data

import com.pepe.mycars.app.data.domain.repository.DataRepository
import com.pepe.mycars.app.utils.state.ItemModelState
import kotlinx.coroutines.flow.Flow

class AddItemUseCase(
    private val dataRepository: DataRepository
) {

    fun execute(params: Param): Flow<ItemModelState> = dataRepository.addRefillItem(
        params.currMileage,
        params.fuelCost,
        params.fuelAmount,
        params.refillDate,
        params.notes
    )

    data class Param(
        val currMileage: Float,
        val fuelCost: Float,
        val fuelAmount: Float,
        val refillDate: String,
        val notes: String
    )

}