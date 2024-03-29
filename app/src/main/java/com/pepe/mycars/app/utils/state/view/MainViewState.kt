package com.pepe.mycars.app.utils.state.view

import com.pepe.mycars.app.data.local.MainScoreModel

sealed class MainViewState {

    object Loading : MainViewState()
    data class Success(
        val data: MainScoreModel,
        val successMsg: String
    ) : MainViewState()

    data class Error(val errorMsg: String) : MainViewState()
}