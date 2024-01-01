package com.pepe.mycars.app.utils.state

sealed class UserViewState{
    object Loading : UserViewState()
    data class Success(
        val isLoggedIn: Boolean = true,
        val successMsg: String
    ) : UserViewState()

    data class Error(val errorMsg: String) : UserViewState()
}
