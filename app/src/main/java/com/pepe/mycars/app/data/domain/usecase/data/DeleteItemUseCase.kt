package com.pepe.mycars.app.data.domain.usecase.data

import com.pepe.mycars.app.data.domain.repository.DataRepository
import com.pepe.mycars.app.utils.state.ItemModelState
import kotlinx.coroutines.flow.Flow

class DeleteItemUseCase(
    private val dataRepository: DataRepository
) {

    fun execute(params: Param): Flow<ItemModelState> =
        dataRepository.deleteRefillItem(params.itemId)

    data class Param(
        val itemId: String
    )

}