package com.pepe.mycars.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pepe.mycars.app.utils.state.view.LoginViewState
import com.pepe.mycars.domain.repository.IAuthRepository
import com.pepe.mycars.domain.repository.IUserRepository
import com.pepe.mycars.domain.usecase.auth.LoginUseCase
import com.pepe.mycars.domain.usecase.auth.RegisterAsGuestUseCase
import com.pepe.mycars.domain.usecase.auth.RegisterUseCase
import com.pepe.mycars.domain.usecase.auth.SignInWithGoogleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

@HiltViewModel
class AuthViewModel
    @Inject
    constructor(
        private val loginUseCase: LoginUseCase,
        private val registerUseCase: RegisterUseCase,
        private val registerAsGuestUseCase: RegisterAsGuestUseCase,
        private val signInWithGoogleUseCase: SignInWithGoogleUseCase,
        authRepository: IAuthRepository,
        userRepository: IUserRepository,
    ) : ViewModel() {
        private val _loginViewState = MutableStateFlow<LoginViewState>(LoginViewState.Idle)
        val loginViewState: StateFlow<LoginViewState> = _loginViewState.asStateFlow()

        init {
            synchronizeAuth(authRepository, userRepository)
        }

        private fun synchronizeAuth(
            authRepository: IAuthRepository,
            userRepository: IUserRepository,
        ) {
            combine(
                flow = authRepository.validSessionFlow,
                flow2 = userRepository.getSyncFirestoreUserData(),
            ) { isAuthenticated, user ->
                LoginViewState.Success(isLoggedIn = isAuthenticated && user != null)
            }
                .onStart { _loginViewState.value = LoginViewState.Loading }
                .catch { e -> _loginViewState.value = LoginViewState.Error(e.localizedMessage ?: "Unknown error") }
                .distinctUntilChanged()
                .onEach { _loginViewState.value = it }
                .launchIn(viewModelScope)
        }

        fun login(
            email: String?,
            password: String?,
        ) {
            if (!isLoginPossible(email, password)) return
            if (email.isNullOrEmpty()) return
            if (password.isNullOrEmpty()) return
            loginUseCase(email, password)
                .onStart { _loginViewState.value = LoginViewState.Loading }
                .catch { e -> _loginViewState.value = LoginViewState.Error(e.localizedMessage ?: "Unknown error") }
                .onEach { _loginViewState.value = LoginViewState.Success(isLoggedIn = true, successMsg = "Logged in successfully") }
                .launchIn(viewModelScope)
        }

        fun register(
            email: String?,
            password: String?,
            name: String?,
        ) {
            if (email.isNullOrEmpty() || password.isNullOrEmpty() || name.isNullOrEmpty()) {
                _loginViewState.value = LoginViewState.Error("All fields must be filled")
                return
            }
            registerUseCase(email, password, name)
                .onStart { _loginViewState.value = LoginViewState.Loading }
                .catch { e -> _loginViewState.value = LoginViewState.Error(e.localizedMessage ?: "Unknown error") }
                .onEach { _loginViewState.value = LoginViewState.Success(isLoggedIn = true, successMsg = "New account created") }
                .launchIn(viewModelScope)
        }

        fun registerAsGuest() {
            registerAsGuestUseCase()
                .onStart { _loginViewState.value = LoginViewState.Loading }
                .catch { e -> _loginViewState.value = LoginViewState.Error(e.localizedMessage ?: "Error") }
                .onEach { _loginViewState.value = LoginViewState.Success(isLoggedIn = true, successMsg = "Logged as guest") }
                .launchIn(viewModelScope)
        }

        fun signInWithGoogle(
            idToken: String,
            userName: String,
            email: String,
        ) {
            signInWithGoogleUseCase(idToken, userName, email)
                .onStart { _loginViewState.value = LoginViewState.Loading }
                .catch { e -> _loginViewState.value = LoginViewState.Error(e.localizedMessage ?: "Unknown error") }
                .onEach { _loginViewState.value = LoginViewState.Success(isLoggedIn = true, successMsg = "Logged successfully") }
                .launchIn(viewModelScope)
        }

        private fun isLoginPossible(
            email: String?,
            password: String?,
        ): Boolean {
            val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
            return when {
                password.isNullOrBlank() && email.isNullOrBlank() -> {
                    _loginViewState.value = LoginViewState.Error("Enter email and password")
                    false
                }
                password.isNullOrBlank() -> {
                    _loginViewState.value = LoginViewState.Error("Password field is empty")
                    false
                }
                email.isNullOrBlank() -> {
                    _loginViewState.value = LoginViewState.Error("Email field is empty")
                    false
                }
                !email.trim().matches(emailPattern.toRegex()) -> {
                    _loginViewState.value = LoginViewState.Error("Email field contains wrong characters")
                    false
                }
                else -> true
            }
        }
    }
