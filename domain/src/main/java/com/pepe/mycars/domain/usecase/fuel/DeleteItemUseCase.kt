package com.pepe.mycars.domain.usecase.fuel

import com.pepe.mycars.domain.model.FuelDataInfo
import com.pepe.mycars.domain.repository.IFuelDataRepository
import kotlinx.coroutines.flow.Flow

class DeleteItemUseCase(
    private val fuelDataRepo: IFuelDataRepository,
) {
    fun execute(params: Param): Flow<List<FuelDataInfo>> = fuelDataRepo.deleteRefillItem(params.itemId)

    data class Param(
        val itemId: String,
    )
}