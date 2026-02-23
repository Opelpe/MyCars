package com.pepe.mycars.app.utils.state.view

import com.pepe.mycars.app.data.model.MainScoreModel
import com.pepe.mycars.app.utils.UiText

sealed class MainViewState {
    object Loading : MainViewState()

    data class Success(
        val data: MainScoreModel,
        val successMsg: UiText? = null,
    ) : MainViewState()

    data class Error(val errorMsg: UiText) : MainViewState()
}
