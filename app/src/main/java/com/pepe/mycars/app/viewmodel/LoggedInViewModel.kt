package com.pepe.mycars.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pepe.mycars.app.data.domain.usecase.auth.LogOutUseCase
import com.pepe.mycars.app.utils.state.view.UserViewState
import com.pepe.mycars.data.dto.CommonApiResponse
import com.pepe.mycars.data.firebase.repo.IUserRepository
import com.pepe.mycars.domain.model.AccountProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class LoggedInViewModel
    @Inject
    constructor(
        private val userRepository: IUserRepository,
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
                        CommonApiResponse.Loading -> UserViewState.Loading
                        is CommonApiResponse.Error -> UserViewState.Error(it.message)
                        is CommonApiResponse.Success -> {
                            val isAnonymous = it.data.providerType == AccountProvider.ANONYMOUS
                            UserViewState.Success(
                                isLoggedIn = true,
                                isAnonymous = isAnonymous,
                                successMsg = "",
                            )
                        }
                    }
                _userViewState.postValue(action)
            }.launchIn(viewModelScope)
        }

        fun appStart() {
            userRepository.getLoggedUserData().onEach {
                when (it) {
                    CommonApiResponse.Loading -> _userViewState.postValue(UserViewState.Loading)

                    is CommonApiResponse.Error -> _userViewState.postValue(UserViewState.Error(it.message))

                    is CommonApiResponse.Success -> {
                        val isGuest = it.data.providerType == AccountProvider.ANONYMOUS
                        val isLoggedIn = it.data.autoLogin
                        _userViewState.postValue(UserViewState.Success(isLoggedIn, isGuest, ""))
                    }
                }
            }.launchIn(viewModelScope)
        }
    }
