package com.pepe.mycars.app.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pepe.mycars.app.data.domain.repository.AuthRepository
import com.pepe.mycars.app.data.domain.repository.UserRepository
import com.pepe.mycars.app.utils.Resource
import com.pepe.mycars.app.utils.networkState.UserState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoggedInViewModel
@Inject
constructor(
    private var authRepository: AuthRepository,
    private var userRepository: UserRepository
) : ViewModel() {

    var userStateModel = MutableLiveData(UserState())
    fun logOut() {
        viewModelScope.launch {
            authRepository.logOut()
            userStateModel.value = userStateModel.value?.copy(isLoading = true, isLoggedIn = false)
        }
    }


    fun getUserData() {
        userRepository.getLoggedUserData().onEach {
            when (it) {
                is Resource.Loading -> {
                    viewModelScope.launch {
                        userStateModel.value = userStateModel.value?.copy(isLoading = true)
                    }
                }

                is Resource.Error -> {
                    viewModelScope.launch {
                        userStateModel.value = userStateModel.value?.copy(error = it.message ?: "Unknown error occurred")
                    }
                }

                is Resource.Success -> {
                    viewModelScope.launch {
                        userStateModel.value = userStateModel.value?.copy(data = it.data, isLoggedIn = true)
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

}