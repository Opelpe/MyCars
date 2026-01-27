package com.pepe.mycars.app.utils.state.view

sealed class LoginViewState {
    object Loading : LoginViewState()

    data class Success(
        val isLoggedIn: Boolean = false,
        val successMsg: String? = null,
    ) : LoginViewState()

    data class Error(val errorMsg: String) : LoginViewState()
}
