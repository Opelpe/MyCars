package com.pepe.mycars.app.utils.state


sealed class LoginViewState{
    object Loading : LoginViewState()
    data class Success(
        val isLoggedIn: Boolean = false,
        val successMsg: String
    ) : LoginViewState()

    data class Error(val errorMsg: String) : LoginViewState()
}
