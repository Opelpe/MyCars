package com.pepe.mycars.app.data.domain.repository

import com.pepe.mycars.app.utils.state.UserModelState
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getLoggedUserData(): Flow<UserModelState>

    fun getUserProviderType(): String
}
