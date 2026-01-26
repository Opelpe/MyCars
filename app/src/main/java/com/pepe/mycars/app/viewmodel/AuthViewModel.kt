package com.pepe.mycars.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pepe.mycars.app.utils.state.view.LoginViewState
import com.pepe.mycars.domain.repository.IAuthRepository
import com.pepe.mycars.domain.repository.IUserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel
    @Inject
    constructor(
        private val authRepository: IAuthRepository,
        private val userRepository: IUserRepository,
    ) : ViewModel() {
        private val _loginViewState: MutableLiveData<LoginViewState> =
            MutableLiveData(LoginViewState.Loading)
        val loginViewState: LiveData<LoginViewState> = _loginViewState

        init {
            synchronizeAuth()
        }

        private fun synchronizeAuth() {
            viewModelScope.launch {
                combine(
                    authRepository.validSessionFlow,
                    userRepository.getSyncFirestoreUserData(),
                ) { isAuthenticated, user ->
                    LoginViewState.Success(
                        isLoggedIn = isAuthenticated && user != null,
                        successMsg = "",
                    )
                }
                    .onStart {
                        _loginViewState.postValue(LoginViewState.Loading)
                    }
                    .catch { e ->
                        _loginViewState.postValue(LoginViewState.Error(e.localizedMessage ?: "Unknown error"))
                    }
                    .distinctUntilChanged()
                    .collectLatest { state ->
                        _loginViewState.postValue(state)
                    }
            }
        }

        fun login(
            email: String?,
            password: String?,
            autoLogin: Boolean,
        ) {
            if (isLoginPossible(email, password)) {
                if (email.isNullOrEmpty()) return
                if (password.isNullOrEmpty()) return
                viewModelScope.launch {
                    authRepository.login(email, password, autoLogin)
                        .onStart { _loginViewState.postValue(LoginViewState.Loading) }
                        .catch { e ->
                            _loginViewState.postValue(
                                LoginViewState.Error(e.localizedMessage ?: "Unknown error"),
                            )
                        }
                        .collectLatest {
                            _loginViewState.postValue(
                                LoginViewState.Success(isLoggedIn = true, successMsg = "Logged in successfully"),
                            )
                        }
                }
            }
        }

        fun register(
            email: String?,
            password: String?,
            name: String?,
            autoLogin: Boolean,
        ) {
            if (email!!.isEmpty() || password!!.isEmpty() || name!!.isEmpty()) {
                _loginViewState.postValue(LoginViewState.Error("All fields must be filled"))
                return
            }
            viewModelScope.launch {
                authRepository.register(email, password, name, autoLogin)
                    .onStart { _loginViewState.postValue(LoginViewState.Loading) }
                    .catch { e ->
                        _loginViewState.postValue(
                            LoginViewState.Error(e.localizedMessage ?: "Unknown error"),
                        )
                    }
                    .collectLatest {
                        _loginViewState.postValue(
                            LoginViewState.Success(isLoggedIn = true, successMsg = "New account created"),
                        )
                    }
            }
        }

        fun registerAsGuest() {
            viewModelScope.launch {
                authRepository.registerAsGuest()
                    .onStart { _loginViewState.postValue(LoginViewState.Loading) }
                    .catch { e -> _loginViewState.postValue(LoginViewState.Error(e.localizedMessage ?: "Error")) }
                    .collectLatest {
                        _loginViewState.postValue(
                            LoginViewState.Success(isLoggedIn = true, successMsg = "Logged as guest"),
                        )
                    }
            }
        }

        fun signInWithGoogle(
            idToken: String,
            userName: String,
            email: String,
        ) {
            viewModelScope.launch {
                authRepository.registerWithGoogle(idToken, userName, email)
                    .onStart { _loginViewState.postValue(LoginViewState.Loading) }
                    .catch { e ->
                        _loginViewState.postValue(
                            LoginViewState.Error(e.localizedMessage ?: "Unknown error"),
                        )
                    }
                    .collectLatest {
                        _loginViewState.postValue(
                            LoginViewState.Success(isLoggedIn = true, successMsg = "Logged successfully"),
                        )
                    }
            }
        }

        private fun isLoginPossible(
            email: String?,
            password: String?,
        ): Boolean {
            val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"

            return if (password == null || password.trim().isEmpty()) {
                if (email == null || email.trim().isEmpty()) {
                    _loginViewState.postValue(LoginViewState.Error("Enter email and password"))
                    false
                } else {
                    _loginViewState.postValue(LoginViewState.Error("Password field is empty"))
                    false
                }
            } else if (email == null || email.trim().isEmpty()) {
                _loginViewState.postValue(LoginViewState.Error("Email field is empty"))
                false
            } else if (!email.trim().matches(emailPattern.toRegex())) {
                _loginViewState.postValue(LoginViewState.Error("Email field contains wrong characters"))
                false
            } else {
                true
            }
        }
    }
