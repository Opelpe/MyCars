package com.pepe.mycars.app.utils.state.view

import com.pepe.mycars.app.utils.UiText

sealed interface LoginViewState {
    data object Idle : LoginViewState

    data object Loading : LoginViewState

    data class Success(
        val isLoggedIn: Boolean = false,
        val successMsg: UiText? = null,
    ) : LoginViewState

    data class Error(val message: UiText) : LoginViewState
}
