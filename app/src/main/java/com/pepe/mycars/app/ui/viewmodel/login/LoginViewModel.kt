package com.pepe.mycars.app.ui.viewmodel.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pepe.mycars.app.data.domain.repository.UserRepository
import com.pepe.mycars.app.ui.view.login.LoginViewModelState
import com.pepe.mycars.app.ui.view.splash.SplashViewModelState
import dagger.Lazy
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val repository: Lazy<UserRepository>) :
    ViewModel() {

    val viewState = MutableLiveData(LoginViewModelState())

    fun onGuestButtonClicked(){
        viewModelScope.launch {
            repository.get().setUserLogged(true)
            viewState.value = viewState.value?.copy(loggedIn = true, showToastMessage = true, toastMessage = "zalogowano jako gość")

        }
    }
}