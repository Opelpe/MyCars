package com.pepe.mycars.domain.usecase.auth

import com.pepe.mycars.domain.repository.IAuthRepository

class LogOutUseCase(
    private val authRepository: IAuthRepository,
) {
    fun execute() = authRepository.logOut()
}