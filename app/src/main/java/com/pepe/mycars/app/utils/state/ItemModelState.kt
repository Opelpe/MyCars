package com.pepe.mycars.app.utils.state

import com.pepe.mycars.app.data.model.HistoryItemModel

sealed class ItemModelState{
    object Loading : ItemModelState()

    data class Error(val exceptionMsg: String) : ItemModelState()

    data class Success(val model: List<HistoryItemModel>) : ItemModelState()

}
