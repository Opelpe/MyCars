package com.pepe.mycars.app.utils.state.view

sealed interface LoginViewState {
    data object Idle : LoginViewState

    data object Loading : LoginViewState

    data class Success(
        val isLoggedIn: Boolean = false,
        val successMsg: String = "",
    ) : LoginViewState

    data class Error(val message: String) : LoginViewState
}
