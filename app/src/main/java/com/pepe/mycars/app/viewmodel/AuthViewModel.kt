package com.pepe.mycars.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthCredential
import com.pepe.mycars.app.data.domain.repository.AuthRepository
import com.pepe.mycars.app.utils.Resource
import com.pepe.mycars.app.utils.ResourceOld
import com.pepe.mycars.app.utils.networkState.AuthState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private var authRepository: AuthRepository
) : ViewModel() {

    private val _authState: MutableLiveData<AuthState> = MutableLiveData(AuthState.Loading)
    val authState: LiveData<AuthState> = _authState

    fun login(email: String?, password: String?, autoLogin: Boolean) {
        authRepository.login(email, password, autoLogin).onEach {
            when (it) {
                is ResourceOld.Loading -> {
                    _authState.postValue(AuthState.Loading)
                }

                is ResourceOld.Error -> {
                    _authState.postValue(AuthState.Error(it.message ?: "Unknown error"))
                }

                is ResourceOld.Success -> {
                    _authState.postValue(AuthState.Success(true, "Logged in"))
                }
            }
        }.launchIn(viewModelScope)
    }

    fun register(email: String?, password: String?, name: String?, autoLogin: Boolean) {
        if (email!!.isEmpty() || password!!.isEmpty() || name!!.isEmpty()) {
            _authState.postValue(AuthState.Error("All fields must be filled"))
        } else {
            authRepository.register(email, password, name, autoLogin).onEach {
                when (it) {
                    Resource.Loading -> _authState.postValue(AuthState.Loading)

                    is Resource.Error -> {
                        _authState.postValue(AuthState.Error(it.exceptionMsg ?: "Unknown error"))
                    }

                    is Resource.Success -> {
                        _authState.postValue(AuthState.Success(true, "Logged in"))
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    fun registerAsGuest(autoLogin: Boolean) {
        authRepository.registerAsGuest(autoLogin).onEach {
            when (it) {
                is ResourceOld.Loading -> {
                    _authState.postValue(AuthState.Loading)
                }
                is ResourceOld.Error -> {
                    _authState.postValue(AuthState.Error(it.message?:"Unknown error"))
                }
                is ResourceOld.Success -> {
                    _authState.postValue(AuthState.Success(true, "Logged in"))
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
                is ResourceOld.Loading -> {
                    _authState.postValue(AuthState.Loading)
                }
                is ResourceOld.Error -> {
                    _authState.postValue(AuthState.Error(it.message?:"Unknown error"))
                }
                is ResourceOld.Success -> {
                    _authState.postValue(AuthState.Success(true, "Logged in"))
                }
            }
        }.launchIn(viewModelScope)
    }

    fun checkAuthInfo() {
//        _authState.postValue(AuthState.Error("Unknown error"))
        authRepository.getLoggedUser().onEach {
            when (it) {
                is ResourceOld.Loading -> {
                    _authState.postValue(AuthState.Loading)
                }
                is ResourceOld.Error -> {
                    _authState.postValue(AuthState.Error(it.message?:"Unknown error"))
                }
                is ResourceOld.Success -> {
                    _authState.postValue(AuthState.Success(false, ""))
                }
            }
        }.launchIn(viewModelScope).run {
            _authState.postValue(AuthState.Error("Unknown error"))
        }
    }

}