package com.pepe.mycars.app.utils.state.view

sealed class AddItemViewState {
    object Loading : AddItemViewState()
    data class Success(
        val successMsg: String
    ) : AddItemViewState()

    data class Error(val errorMsg: String) : AddItemViewState()
}
