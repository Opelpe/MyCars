package com.pepe.mycars.domain.usecase.auth

import com.pepe.mycars.domain.repository.IAuthRepository
import kotlinx.coroutines.flow.Flow

class LoginUseCase(
    private val authRepository: IAuthRepository,
) {
    operator fun invoke(email: String, password: String): Flow<Boolean> =
        authRepository.login(email, password)
}
