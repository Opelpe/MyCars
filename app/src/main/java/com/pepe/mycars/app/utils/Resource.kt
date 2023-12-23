package com.pepe.mycars.app.utils

import com.google.firebase.auth.FirebaseUser

sealed class Resource {
    object Loading : Resource()

    data class Error(val exceptionMsg: String) : Resource()

    data class Success(val firebaseUser: FirebaseUser?) : Resource()
}
