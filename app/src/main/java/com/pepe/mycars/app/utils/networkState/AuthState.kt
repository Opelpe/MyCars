package com.pepe.mycars.app.utils.networkState


sealed class AuthState{
    object Loading : AuthState()
    data class Success(
        val isLoggedIn: Boolean = false,
        val successMsg: String
    ) : AuthState()

    data class Error(val errorMsg: String) : AuthState()
}
