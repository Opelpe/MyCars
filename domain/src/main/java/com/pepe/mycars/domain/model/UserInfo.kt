package com.pepe.mycars.domain.model

data class UserInfo(
    val id: String,
    val name: String,
    val email: String,
    val active: Boolean,
    val country: String,
    val providerType: String,
    val autoLogin: Boolean,
)
