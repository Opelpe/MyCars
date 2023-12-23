package com.pepe.mycars.app.data.domain.repository

import com.pepe.mycars.app.data.model.UserModel
import com.pepe.mycars.app.utils.ResourceOld
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getLoggedUserData() : Flow<ResourceOld<UserModel>>

    fun getUserProviderType() : Flow<ResourceOld<String>>

}