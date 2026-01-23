package com.pepe.mycars.app.data.domain.usecase.auth

import com.pepe.mycars.data.firebase.repo.IAuthRepository

class LogOutUseCase(
    private val authRepository: IAuthRepository,
) {
    fun execute() = authRepository.logOut()
}
