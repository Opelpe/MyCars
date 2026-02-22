package com.pepe.mycars.domain.usecase.auth

import com.pepe.mycars.domain.repository.IAuthRepository
import kotlinx.coroutines.flow.Flow

class RegisterUseCase(
    private val authRepository: IAuthRepository,
) {
    operator fun invoke(email: String, password: String, name: String): Flow<Boolean> =
        authRepository.register(email, password, name)
}
