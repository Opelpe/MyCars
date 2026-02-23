package com.pepe.mycars.domain.usecase.auth

import com.pepe.mycars.domain.repository.IAuthRepository
import kotlinx.coroutines.flow.Flow

class SignInWithGoogleUseCase(
    private val authRepository: IAuthRepository,
) {
    operator fun invoke(idToken: String, userName: String, email: String): Flow<Boolean> =
        authRepository.registerWithGoogle(idToken, userName, email)
}
