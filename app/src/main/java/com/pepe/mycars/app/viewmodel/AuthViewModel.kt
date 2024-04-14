package com.pepe.mycars.app.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthCredential
import com.pepe.mycars.app.data.domain.repository.AuthRepository
import com.pepe.mycars.app.utils.state.AuthState
import com.pepe.mycars.app.utils.state.view.LoginViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _loginViewState: MutableLiveData<LoginViewState> =
        MutableLiveData(LoginViewState.Loading)
    val loginViewState: LiveData<LoginViewState> = _loginViewState

    fun login(email: String?, password: String?, autoLogin: Boolean) {

        if (isLoginPossible(email, password)) {
            authRepository.login(email!!, password!!, autoLogin).onEach {
                when (it) {
                    AuthState.Loading -> _loginViewState.postValue(LoginViewState.Loading)

                    is AuthState.Error -> _loginViewState.postValue(LoginViewState.Error(it.exceptionMsg))

                    is AuthState.Success -> {
                        _loginViewState.postValue(LoginViewState.Success(true, "Logged in successfully"))
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    fun register(email: String?, password: String?, name: String?, autoLogin: Boolean) {
        if (email!!.isEmpty() || password!!.isEmpty() || name!!.isEmpty()) {
            _loginViewState.postValue(LoginViewState.Error("All fields must be filled"))
        } else {
            authRepository.register(email, password, name, autoLogin).onEach {
                when (it) {
                    AuthState.Loading -> _loginViewState.postValue(LoginViewState.Loading)

                    is AuthState.Error -> {
                        _loginViewState.postValue(LoginViewState.Error(it.exceptionMsg))
                    }

                    is AuthState.Success -> {
                        _loginViewState.postValue(LoginViewState.Success(true, "New account created"))
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    fun registerAsGuest(autoLogin: Boolean) {
        authRepository.registerAsGuest(autoLogin).onEach {
            when (it) {
                AuthState.Loading -> _loginViewState.postValue(LoginViewState.Loading)

                is AuthState.Error -> _loginViewState.postValue(LoginViewState.Error(it.exceptionMsg))

                is AuthState.Success -> {
                    _loginViewState.postValue(LoginViewState.Success(true, "Logged in as guest"))
                }
            }
        }.launchIn(viewModelScope)
    }

    fun signInWithGoogle(
        credential: AuthCredential,
        userName: String,
        email: String,
        autoLogin: Boolean
    ) {
        authRepository.registerWithGoogle(credential, userName, email, autoLogin).onEach {
            when (it) {
                AuthState.Loading -> _loginViewState.postValue(LoginViewState.Loading)

                is AuthState.Error -> _loginViewState.postValue(LoginViewState.Error(it.exceptionMsg))

                is AuthState.Success -> {
                    _loginViewState.postValue(LoginViewState.Success(true, "Logged successfully"))
                }
            }
        }.launchIn(viewModelScope)
    }

    fun synchronizeAuth() {
        authRepository.getLoggedUser().onEach {
            when (it) {
                AuthState.Loading -> _loginViewState.postValue(LoginViewState.Loading)

                is AuthState.Error -> _loginViewState.postValue(LoginViewState.Error(it.exceptionMsg))

                is AuthState.Success -> _loginViewState.postValue(LoginViewState.Success(false, ""))
            }
        }.launchIn(viewModelScope)
    }

    private fun isLoginPossible(email: String?, password: String?): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"

        return if (password == null || password.trim().isEmpty()) if (email == null || email.trim().isEmpty()) {
            _loginViewState.postValue(LoginViewState.Error("Enter email and password"))
            false
        } else {
            _loginViewState.postValue(LoginViewState.Error("Password field is empty"))
            false
        } else if (email == null || email.trim().isEmpty()) {
            _loginViewState.postValue(LoginViewState.Error("Email field is empty"))
            false
        } else if (!email.trim().matches(emailPattern.toRegex())) {
            _loginViewState.postValue(LoginViewState.Error("Email field contains wrong characters"))
            false
        } else true
    }

}