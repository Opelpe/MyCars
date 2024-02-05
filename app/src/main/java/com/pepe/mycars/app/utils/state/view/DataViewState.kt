package com.pepe.mycars.app.utils.state.view

sealed class DataViewState{
    object Loading : DataViewState()
    data class Success(
        val successMsg: String
    ) : DataViewState()

    data class Error(val errorMsg: String) : DataViewState()
}
