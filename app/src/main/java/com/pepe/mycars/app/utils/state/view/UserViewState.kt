package com.pepe.mycars.app.utils.state.view

import com.pepe.mycars.app.utils.UiText

sealed class UserViewState {
    object Loading : UserViewState()

    data class Success(
        val isLoggedIn: Boolean = true,
        val successMsg: UiText? = null,
    ) : UserViewState()

    data class Error(val errorMsg: UiText) : UserViewState()
}
