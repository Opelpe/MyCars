package com.pepe.mycars.app.utils.state
import com.pepe.mycars.app.data.model.UserModel

sealed class UserModelState{
    object Loading : UserModelState()

    data class Error(val exceptionMsg: String) : UserModelState()

    data class Success(val userModel: UserModel?) : UserModelState()
}
