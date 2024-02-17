package com.pepe.mycars.app.utils.state.view

import com.pepe.mycars.app.data.local.HistoryItemUiModel
import com.pepe.mycars.app.viewmodel.HistoryOperations

sealed class HistoryItemViewState{
    object Loading : HistoryItemViewState()
    data class Success(
        val data: List<HistoryItemUiModel>,
        val successMsg: String,
        val historyOperations: HistoryOperations
    ) : HistoryItemViewState()

    data class Error(val errorMsg: String, val historyOperations: HistoryOperations) : HistoryItemViewState()
}
