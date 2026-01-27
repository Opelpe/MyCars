package com.pepe.mycars.domain.repository

import com.pepe.mycars.domain.model.UserInfo
import kotlinx.coroutines.flow.Flow

interface IUserRepository {

    fun getSyncFirestoreUserData(): Flow<UserInfo?>

    fun getUserProviderType(): String

    fun getUserAutoLogin(): Boolean
}
