package com.pepe.mycars.app.utils.state

import com.pepe.mycars.app.data.model.ItemRefillModel

sealed class ItemModelState{
    object Loading : ItemModelState()

    data class Error(val exceptionMsg: String) : ItemModelState()

    data class Success(val model: List<ItemRefillModel>) : ItemModelState()

}
