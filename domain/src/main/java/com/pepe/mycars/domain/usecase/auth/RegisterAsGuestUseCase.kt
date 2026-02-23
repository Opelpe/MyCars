package com.pepe.mycars.domain.usecase.auth

import com.pepe.mycars.domain.repository.IAuthRepository
import kotlinx.coroutines.flow.Flow

class RegisterAsGuestUseCase(
    private val authRepository: IAuthRepository,
) {
    operator fun invoke(): Flow<Boolean> = authRepository.registerAsGuest()
}
