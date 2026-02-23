package com.pepe.mycars.app.utils.state.view

import com.pepe.mycars.app.utils.UiText
import com.pepe.mycars.app.viewmodel.RefillOperations
import com.pepe.mycars.domain.model.FuelDataInfo

sealed class RefillItemViewState {
    object Idle : RefillItemViewState()

    object Loading : RefillItemViewState()

    data class Success(
        val item: FuelDataInfo?,
        val operations: RefillOperations?,
        val successMsg: UiText? = null,
    ) : RefillItemViewState()

    data class Error(val errorMsg: UiText) : RefillItemViewState()
}
