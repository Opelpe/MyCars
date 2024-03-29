package com.pepe.mycars.app.data.domain.usecase.auth

import com.pepe.mycars.app.data.domain.repository.AuthRepository

class LogOutUseCase(
    private val authRepository: AuthRepository
) {
    fun execute() = authRepository.logOut()

}