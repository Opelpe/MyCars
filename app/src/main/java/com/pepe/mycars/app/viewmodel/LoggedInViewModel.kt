package com.pepe.mycars.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pepe.mycars.app.data.domain.repository.AuthRepository
import com.pepe.mycars.app.data.domain.repository.UserRepository
import com.pepe.mycars.app.utils.Resource
import com.pepe.mycars.app.utils.networkState.UserState
import com.pepe.mycars.app.utils.networkState.UserViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoggedInViewModel
@Inject
constructor(
    //todo dlaczego to jest var? czemu nie korzystasz z inmutableowości kotlina?? to zapobiega nulpotierom
    private var authRepository: AuthRepository,
    private var userRepository: UserRepository
) : ViewModel() {


    //todo powinny byc dwie zmienne. to jak ty masz zrobione to widok jest w stanie zmienić wartości w tej live dacie.
    // mutable powinna byc tylko dla view modela a wystawiona na zewnątrz nie powinna być jako LiveData po prostu.
    //  private val _userStateModel = MutableLiveData(UserState())
    //    val userStateModel: LiveData <UserState> = _userStateModel

    var userStateModel = MutableLiveData(UserState())

    var userStateModel2:LiveData<UserViewState> = MutableLiveData(UserViewState.Loading)
    fun logOut() {
        viewModelScope.launch {
            authRepository.logOut()
            // todo  _userStateModel.value = userStateModel.value?.copy(isLoading = true, isLoggedIn = false)
            userStateModel.value = userStateModel.value?.copy(isLoading = true, isLoggedIn = false)
        }
    }


    fun getUserData() {
        userRepository.getLoggedUserData().onEach {
            when (it) {
                is Resource.Loading -> {
                    //todo po co
                    viewModelScope.launch {

                        //todo w perfekcyjnym mogłbbyś stworzyć maper dla live daty i mieć wtedy coś w stylu mapper
                        // userStateModel.value = mapper.toggleLoading() -> testowlanośc kodu wzrasta w chuj
                        userStateModel.value = userStateModel.value?.copy(isLoading = true)
                    }
                }

                is Resource.Error -> {
                    //todo po co
                    viewModelScope.launch {
                        userStateModel.value = userStateModel.value?.copy(error = it.message ?: "Unknown error occurred")
                    }
                }

                is Resource.Success -> {
                    //todo po co
                    viewModelScope.launch {
                        userStateModel.value =
                            userStateModel.value?.copy(data = it.data, isLoggedIn = true)
                    }
                }
            }
            //todo po co

        }.launchIn(viewModelScope)
    }

}