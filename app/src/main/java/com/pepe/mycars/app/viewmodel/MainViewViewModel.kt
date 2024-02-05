package com.pepe.mycars.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pepe.mycars.app.data.domain.repository.UserRepository
import com.pepe.mycars.app.utils.FireStoreUserDocField
import com.pepe.mycars.app.utils.state.UserModelState
import com.pepe.mycars.app.utils.state.view.UserViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class MainViewViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel (){

    private val _userMainViewState: MutableLiveData<UserViewState> =
        MutableLiveData(UserViewState.Loading)
    val userMainViewState: LiveData<UserViewState> = _userMainViewState
    fun getUserSyncState() {
            userRepository.getLoggedUserData().onEach {
                when (it) {
                    UserModelState.Loading -> {}

                    is UserModelState.Error -> _userMainViewState.postValue(UserViewState.Error(it.exceptionMsg))

                    is UserModelState.Success -> {
                        val isGuest = it.userModel!!.providerType == FireStoreUserDocField.ACCOUNT_PROVIDER_ANONYMOUS
                        _userMainViewState.postValue(
                            UserViewState.Success(
                                true,
                                isGuest,
                                ""
                            )
                        )
                    }

                }
            }.launchIn(viewModelScope)
    }

    fun actionSynchronize() {
        userRepository.getUserProviderType().onEach {
            _userMainViewState.postValue(
                UserViewState.Error(
                    getActionSynchronizeResponse(
                        it
                    )
                )
            )
        }.launchIn(viewModelScope)
    }

    private fun getActionSynchronizeResponse(response: String): String {
        return if (response == FireStoreUserDocField.ACCOUNT_PROVIDER_ANONYMOUS || response == FireStoreUserDocField.ACCOUNT_PROVIDER_EMAIL || response == FireStoreUserDocField.ACCOUNT_PROVIDER_GOOGLE) {
            if (response == FireStoreUserDocField.ACCOUNT_PROVIDER_ANONYMOUS) "Sign in & Synchronize data" else "Your data is synchronized"
        } else {
            response
        }
    }
}