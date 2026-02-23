package com.pepe.mycars.app.utils.state.view

import com.pepe.mycars.app.data.model.HistoryItemUiModel
import com.pepe.mycars.app.utils.UiText

sealed class HistoryItemViewState {
    object Loading : HistoryItemViewState()

    data class Success(
        val data: List<HistoryItemUiModel>,
        val successMsg: UiText? = null,
    ) : HistoryItemViewState()

    data class Error(val errorMsg: UiText) : HistoryItemViewState()
}
