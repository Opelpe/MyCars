package com.pepe.mycars.domain.usecase.fuel

import com.pepe.mycars.domain.model.FuelDataInfo
import com.pepe.mycars.domain.repository.IFuelDataRepository
import kotlinx.coroutines.flow.Flow

class GetRefillItemsUseCase(
    private val fuelDataRepo: IFuelDataRepository,
) {
    fun execute(): Flow<List<FuelDataInfo>> = fuelDataRepo.getUserItems()
}