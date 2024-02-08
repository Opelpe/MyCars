package com.pepe.mycars.app.utils.state.view

import com.pepe.mycars.app.data.local.HistoryItemUiModel

sealed class HistoryItemViewState{
    object Loading : HistoryItemViewState()
    data class Success(
        val data: List<HistoryItemUiModel>,
        val successMsg: String
    ) : HistoryItemViewState()

    data class Error(val errorMsg: String) : HistoryItemViewState()
}
