package com.pepe.mycars.app.utils.networkState

import com.pepe.mycars.app.data.model.UserModel

//todo czy to jest State czy ViewState?
data class UserState(
    val data: UserModel? = null,
    val error: String = "",
    //todo czemu masz jeden var i reszte valów?
    var isLoading: Boolean = false,
    val isLoggedIn: Boolean = true
){
    //todo czy przypadkiem to nie jest prawdą? jeśli jest to po co to ręcznie obslugiwać???
   // var isLoading: Boolean = data != null

}


sealed class UserViewState {
    object Loading : UserViewState()
    data class Success(
        val data: UserModel,
        val isLoggedIn: Boolean = true
    ) : UserViewState(){

    }

    data class Error(val msg: String) : UserViewState()
}