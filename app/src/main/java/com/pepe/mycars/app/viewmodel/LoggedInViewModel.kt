package com.pepe.mycars.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pepe.mycars.app.data.domain.repository.UserRepository
import com.pepe.mycars.app.data.domain.usecase.auth.LogOutUseCase
import com.pepe.mycars.app.utils.FireStoreUserDocField.ACCOUNT_PROVIDER_ANONYMOUS
import com.pepe.mycars.app.utils.state.UserModelState
import com.pepe.mycars.app.utils.state.view.UserViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class LoggedInViewModel
    @Inject
    constructor(
        private val userRepository: UserRepository,
        private val logOutUseCase: LogOutUseCase,
    ) : ViewModel() {
        private val _userViewState: MutableLiveData<UserViewState> =
            MutableLiveData(UserViewState.Loading)
        val userViewState: LiveData<UserViewState> = _userViewState

        fun logOut() {
            logOutUseCase.execute()
        }

        fun getUserData() {
            userRepository.getLoggedUserData().onEach {
                val action =
                    when (it) {
                        UserModelState.Loading -> UserViewState.Loading
                        is UserModelState.Error -> UserViewState.Error(it.exceptionMsg)
                        is UserModelState.Success ->
                            UserViewState.Success(
                                true,
                                it.userModel!!.providerType == ACCOUNT_PROVIDER_ANONYMOUS,
                                "",
                            )
                    }
                _userViewState.postValue(action)
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
                            _userViewState.postValue(UserViewState.Success(true, isGuest, ""))
                        } else {
                            _userViewState.postValue(UserViewState.Success(false, isGuest, ""))
                        }
                    }
                }
            }.launchIn(viewModelScope)
        }
    }
