package com.pepe.mycars.app.utils.state.view

import com.pepe.mycars.app.data.model.HistoryItemModel
import com.pepe.mycars.app.viewmodel.RefillOperations

sealed class RefillItemViewState {
    object Loading : RefillItemViewState()

    data class Success(
        val item: HistoryItemModel?,
        val operations: RefillOperations?,
        val successMsg: String,
    ) : RefillItemViewState()

    data class Error(val errorMsg: String) : RefillItemViewState()
}
