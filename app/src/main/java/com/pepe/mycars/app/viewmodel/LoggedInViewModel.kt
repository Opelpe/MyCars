package com.pepe.mycars.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pepe.mycars.app.data.domain.repository.AuthRepository
import com.pepe.mycars.app.data.domain.repository.UserRepository
import com.pepe.mycars.app.data.model.UserModel
import com.pepe.mycars.app.utils.FireStoreDocumentField.ACCOUNT_PROVIDER_ANONYMOUS
import com.pepe.mycars.app.utils.FireStoreDocumentField.ACCOUNT_PROVIDER_GOOGLE
import com.pepe.mycars.app.utils.ResourceOld
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


    //todo powinny byc dwie zmienne. to jak ty masz zrobione to widok jest w stanie zmienić wartości w tej live dacie.
    // mutable powinna byc tylko dla view modela a wystawiona na zewnątrz nie powinna być jako LiveData po prostu.
    //  private val _userStateModel = MutableLiveData(UserState())
    //    val userStateModel: LiveData <UserState> = _userStateModel

    private val _userViewState: MutableLiveData<UserViewState> = MutableLiveData(UserViewState.Loading)
    val userViewState: LiveData<UserViewState> = _userViewState


    private var userData: UserModel? = null
    fun logOut() {

        _userViewState.postValue(UserViewState.Loading)
        authRepository.logOut()
        userData = null
        _userViewState.postValue(UserViewState.Success(false, "Successfully logged out!"))
    }


    fun getUserData() {
        userRepository.getLoggedUserData().onEach {
            when (it) {
                is ResourceOld.Loading -> {
                    _userViewState.postValue(UserViewState.Loading)
                    //todo w perfekcyjnym mogłbbyś stworzyć maper dla live daty i mieć wtedy coś w stylu mapper
                    // userStateModel.value = mapper.toggleLoading() -> testowlanośc kodu wzrasta w chuj
                }

                is ResourceOld.Error -> {
                    _userViewState.postValue(
                        UserViewState.Error(
                            it.message ?: "Unknown error occurred"
                        )
                    )
                }

                is ResourceOld.Success -> {
                    userData = it.data
                    val msg = createLoggedAsMsg(
                        userData!!.providerType,
                        userData!!.email,
                        userData!!.name
                    )
                    _userViewState.postValue(UserViewState.Success(true, msg))
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun createLoggedAsMsg(providerType: String, email: String, name: String): String {
        var msg = "Logged as: "
        msg += if (email.isNotEmpty()) "$email \n $providerType" else name
        return msg
    }

    fun appStart() {
        userRepository.getLoggedUserData().onEach {
            when (it) {
                is ResourceOld.Loading -> {
                    _userViewState.postValue(UserViewState.Loading)
                }

                is ResourceOld.Error -> {
                    _userViewState.postValue(
                        UserViewState.Error(
                            it.message ?: "Unknown error occurred"
                        )
                    )
                }

                is ResourceOld.Success -> {
                    userData = it.data
                    val autoLogin = it.data!!.autoLogin
                    if (autoLogin) {
                        _userViewState.postValue(UserViewState.Success(true, ""))
                    } else {
                        _userViewState.postValue(UserViewState.Success(false, ""))
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    fun actionSynchronize(actionId: Int) {
        userRepository.getUserProviderType().onEach {
            when (it) {
                is ResourceOld.Loading -> {
                    _userViewState.postValue(UserViewState.Loading)
                }

                is ResourceOld.Error -> {
                    _userViewState.postValue(
                        UserViewState.Error(
                            it.message ?: "Unknown error occurred"
                        )
                    )
                }

                is ResourceOld.Success -> {
                    val provider = it.data!!
                    when (actionId) {
                        1 -> {
                            if (provider == ACCOUNT_PROVIDER_ANONYMOUS) {
                                _userViewState.postValue(
                                    UserViewState.Success(
                                        true,
                                        "Sign in & Synchronize data"
                                    )
                                )
                            } else {
                                _userViewState.postValue(
                                    UserViewState.Success(
                                        true,
                                        "Your data is synchronized"
                                    )
                                )
                            }
                        }

                        2 -> {
                            if (provider == ACCOUNT_PROVIDER_GOOGLE) {
                                _userViewState.postValue(
                                    UserViewState.Success(
                                        true,
                                        "You're already sign with Google"
                                    )
                                )
                            } else {
                                _userViewState.postValue(
                                    UserViewState.Success(
                                        true,
                                        "Connect your account with Google"
                                    )
                                )
                            }
                        }
                    }
                }
            }

        }
    }


}