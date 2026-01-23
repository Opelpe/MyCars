package com.pepe.mycars.data.dto

import com.pepe.mycars.domain.model.UserInfo

data class UserDto(
    val name: String? = null,
    val email: String? = null,
    val active: Boolean? = null,
    val country: String? = null,
    val providerType: String? = null,
    val id: String? = null,
    val autoLogin: Boolean? = null,
) {
    fun toDomain(): UserInfo {
        return UserInfo(
            id = id ?: "",
            name = name ?: "",
            email = email ?: "",
            active = active ?: false,
            country = country ?: "",
            providerType = providerType ?: "",
            autoLogin = autoLogin ?: false,
        )
    }

    companion object {
        fun fromDomain(userInfo: UserInfo): UserDto {
            return UserDto(
                id = userInfo.id,
                name = userInfo.name,
                email = userInfo.email,
                active = userInfo.active,
                country = userInfo.country,
                providerType = userInfo.providerType,
                autoLogin = userInfo.autoLogin,
            )
        }
    }
}
