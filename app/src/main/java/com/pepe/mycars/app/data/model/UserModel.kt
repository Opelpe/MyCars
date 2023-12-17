package com.pepe.mycars.app.data.model
data class UserModel(
    val name: String = "",
    val email: String = "",
    val active: Boolean = false,
    val country: String = "",
    val providerType: String = "",
    val id: String = "",
    val autoLogin: Boolean = false
) {
}