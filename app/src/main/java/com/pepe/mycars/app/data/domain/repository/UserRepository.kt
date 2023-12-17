package com.pepe.mycars.app.data.domain.repository

import com.pepe.mycars.app.data.model.UserModel
import com.pepe.mycars.app.utils.Resource
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getLoggedUserData() : Flow<Resource<UserModel>>

}