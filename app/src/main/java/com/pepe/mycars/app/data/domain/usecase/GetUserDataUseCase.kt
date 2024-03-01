package com.pepe.mycars.app.data.domain.usecase

import com.pepe.mycars.app.data.domain.repository.DataRepository
import com.pepe.mycars.app.utils.state.ItemModelState
import kotlinx.coroutines.flow.Flow

class GetUserDataUseCase(
    private val dataRepository: DataRepository
) {

    fun execute(): Flow<ItemModelState> = dataRepository.getUserItems()

}