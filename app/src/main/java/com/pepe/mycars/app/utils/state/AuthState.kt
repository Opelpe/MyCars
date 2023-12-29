package com.pepe.mycars.app.utils.state

import com.google.firebase.auth.FirebaseUser

sealed class AuthState {
    object Loading : AuthState()

    data class Error(val exceptionMsg: String) : AuthState()

    data class Success(val firebaseUser: FirebaseUser?) : AuthState()
}
