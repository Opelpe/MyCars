package com.pepe.mycars.data.firebase.repo

import com.pepe.mycars.data.dto.CommonApiResponse
import com.pepe.mycars.domain.model.UserInfo
import kotlinx.coroutines.flow.Flow

interface IUserRepository {
    fun getLoggedUserData(): Flow<CommonApiResponse<UserInfo>>

    fun getUserProviderType(): String

    fun getUserAutoLogin(): Boolean
}
