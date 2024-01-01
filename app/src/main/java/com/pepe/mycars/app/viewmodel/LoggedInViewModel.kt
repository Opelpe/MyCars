package com.pepe.mycars.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pepe.mycars.app.data.domain.repository.AuthRepository
import com.pepe.mycars.app.data.domain.repository.UserRepository
import com.pepe.mycars.app.utils.FireStoreDocumentField.ACCOUNT_PROVIDER_ANONYMOUS
import com.pepe.mycars.app.utils.FireStoreDocumentField.ACCOUNT_PROVIDER_EMAIL
import com.pepe.mycars.app.utils.FireStoreDocumentField.ACCOUNT_PROVIDER_GOOGLE
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
        _userViewState.postValue(UserViewState.Success(false, "Successfully logged out!"))
    }

    fun getUserData() {
        userRepository.getLoggedUserData().onEach {
            when (it) {
                UserModelState.Loading -> _userViewState.postValue(UserViewState.Loading)

                is UserModelState.Error -> _userViewState.postValue(UserViewState.Error(it.exceptionMsg))

                is UserModelState.Success -> _userViewState.postValue(UserViewState.Success(true, ""))
            }
            //todo w perfekcyjnym mogłbbyś stworzyć maper dla live daty i mieć wtedy coś w stylu mapper
            // userStateModel.value = mapper.toggleLoading() -> testowlanośc kodu wzrasta w chuj
        }.launchIn(viewModelScope)
    }

    fun appStart() {
        userRepository.getLoggedUserData().onEach {
            when (it) {
                UserModelState.Loading -> _userViewState.postValue(UserViewState.Loading)

                is UserModelState.Error -> _userViewState.postValue(UserViewState.Error(it.exceptionMsg))

                is UserModelState.Success -> if (it.userModel!!.autoLogin) {
                    _userViewState.postValue(UserViewState.Success(true, ""))
                } else {
                    _userViewState.postValue(UserViewState.Success(false, ""))
                }
            }
        }.launchIn(viewModelScope)
    }

    fun actionSynchronize(actionId: Int) {
        userRepository.getUserProviderType().onEach {
            _userViewState.postValue(UserViewState.Error(getActionSynchronizeResponse(it, actionId)))
        }.launchIn(viewModelScope)
    }

    private fun getActionSynchronizeResponse(response: String, actionId: Int): String {
        return if (response == ACCOUNT_PROVIDER_ANONYMOUS || response == ACCOUNT_PROVIDER_EMAIL || response == ACCOUNT_PROVIDER_GOOGLE) {
            when (actionId) {
                1 -> if (response == ACCOUNT_PROVIDER_ANONYMOUS) "Sign in & Synchronize data" else "Your data is synchronized"

                2 -> if (response == ACCOUNT_PROVIDER_GOOGLE) "You're already sign with Google" else "Connect your account with Google"

                else -> response
            }
        } else {
            response
        }
    }


}