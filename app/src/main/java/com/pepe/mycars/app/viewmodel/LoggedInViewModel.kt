package com.pepe.mycars.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pepe.mycars.app.data.domain.repository.AuthRepository
import com.pepe.mycars.app.data.domain.repository.UserRepository
import com.pepe.mycars.app.utils.FireStoreDocumentField.ACCOUNT_PROVIDER_ANONYMOUS
import com.pepe.mycars.app.utils.state.UserModelState
import com.pepe.mycars.app.utils.state.UserViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class LoggedInViewModel
@Inject
constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _userViewState: MutableLiveData<UserViewState> =
        MutableLiveData(UserViewState.Loading)
    val userViewState: LiveData<UserViewState> = _userViewState

    fun logOut() {
        _userViewState.postValue(UserViewState.Loading)
        authRepository.logOut()
        _userViewState.postValue(
            UserViewState.Success(
                false,
                false,
                "Successfully logged out!"
            )
        )
    }

    fun getUserData() {
        userRepository.getLoggedUserData().onEach {
            when (it) {
                UserModelState.Loading -> _userViewState.postValue(UserViewState.Loading)

                is UserModelState.Error -> _userViewState.postValue(UserViewState.Error(it.exceptionMsg))

                is UserModelState.Success -> {
                    val isGuest = it.userModel!!.providerType == ACCOUNT_PROVIDER_ANONYMOUS
                    _userViewState.postValue(UserViewState.Success(true, isGuest,  ""))
                }
            }
        }.launchIn(viewModelScope)
    }

    fun appStart() {
        userRepository.getLoggedUserData().onEach {
            when (it) {
                UserModelState.Loading -> _userViewState.postValue(UserViewState.Loading)

                is UserModelState.Error -> _userViewState.postValue(UserViewState.Error(it.exceptionMsg))

                is UserModelState.Success -> {
                    val isGuest = it.userModel!!.providerType == ACCOUNT_PROVIDER_ANONYMOUS
                    if (it.userModel.autoLogin) {
                    _userViewState.postValue(UserViewState.Success(true, isGuest,  ""))
                } else {
                    _userViewState.postValue(UserViewState.Success(false, isGuest,  ""))
                }}
            }
        }.launchIn(viewModelScope)
    }

}