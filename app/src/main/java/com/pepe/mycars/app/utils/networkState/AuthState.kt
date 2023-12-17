package com.pepe.mycars.app.utils.networkState

import com.google.firebase.auth.FirebaseUser
import com.pepe.mycars.app.utils.Resource

data class AuthState(
    val data: FirebaseUser? = null,
    val error: String = "",
    val isLoading: Boolean = false
)
