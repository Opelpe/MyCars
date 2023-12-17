package com.pepe.mycars.app.utils.networkState

import com.pepe.mycars.app.data.model.UserModel
data class UserState(
    val data: UserModel? = null,
    val error: String = "",
    var isLoading: Boolean = false,
    val isLoggedIn: Boolean = true
)
