package com.pepe.mycars.app.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthCredential
import com.pepe.mycars.app.data.domain.repository.AuthRepository
import com.pepe.mycars.app.data.domain.repository.UserRepository
import com.pepe.mycars.app.utils.Resource
import com.pepe.mycars.app.utils.networkState.AuthState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private var authRepository: AuthRepository
) : ViewModel() {

    var authStateModel = MutableLiveData(AuthState())

    fun login(email: String?, password: String?, autoLogin: Boolean) {
        authRepository.login(email, password, autoLogin).onEach {
            when (it) {
                is Resource.Loading -> {
                    viewModelScope.launch{
                        authStateModel.value = authStateModel.value?.copy(isLoading = true)
                    }
                }
                is Resource.Error -> {
                    viewModelScope.launch{
                        authStateModel.value = authStateModel.value?.copy(error = it.message!!)
                    }
                }
                is Resource.Success -> {
                    viewModelScope.launch{
                        authStateModel.value = authStateModel.value?.copy(data = it.data)
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    fun register(email: String?, password: String?, name: String?, autoLogin: Boolean) {
        if (email!!.isEmpty() || password!!.isEmpty() || name!!.isEmpty()) {
            viewModelScope.launch {
                authStateModel.value =
                    AuthState(error = "All fields must be filled", isLoading = false)
            }
        } else {
            authRepository.register(email, password, name, autoLogin).onEach {
                when (it) {
                    is Resource.Loading -> {
                        viewModelScope.launch{
                            authStateModel.value = authStateModel.value?.copy(isLoading = true)
                        }
                    }
                    is Resource.Error -> {
                        viewModelScope.launch{
                            authStateModel.value = authStateModel.value?.copy(error = it.message!!)
                        }
                    }
                    is Resource.Success -> {
                        viewModelScope.launch{
                            authStateModel.value = authStateModel.value?.copy(data = it.data)
                        }
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    fun registerAsGuest(autoLogin: Boolean) {
        authRepository.registerAsGuest(autoLogin).onEach {
            when (it) {
                is Resource.Loading -> {
                    viewModelScope.launch{
                        authStateModel.value = authStateModel.value?.copy(isLoading = true)
                    }
                }
                is Resource.Error -> {
                    viewModelScope.launch{
                        authStateModel.value = authStateModel.value?.copy(error = it.message!!)
                    }
                }
                is Resource.Success -> {
                    viewModelScope.launch{
                        authStateModel.value = authStateModel.value?.copy(data = it.data)
                    }
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
                is Resource.Loading -> {
                    viewModelScope.launch{
                        authStateModel.value = authStateModel.value?.copy(isLoading = true)
                    }
                }
                is Resource.Error -> {
                    viewModelScope.launch{
                        authStateModel.value = authStateModel.value?.copy(error = it.message!!)
                    }
                }
                is Resource.Success -> {
                    viewModelScope.launch{
                        authStateModel.value = authStateModel.value?.copy(data = it.data)
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

}