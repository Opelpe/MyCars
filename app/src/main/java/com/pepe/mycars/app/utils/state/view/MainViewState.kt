package com.pepe.mycars.app.utils.state.view

import com.pepe.mycars.app.data.local.MainViewModel

sealed class MainViewState {

    object Loading : MainViewState()
    data class Success(
        val data: MainViewModel,
        val successMsg: String
    ) : MainViewState()

    data class Error(val errorMsg: String) : MainViewState()
}