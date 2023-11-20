package com.pepe.mycars.app.ui.viewmodel.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pepe.mycars.app.data.domain.repository.UserRepository
import com.pepe.mycars.app.ui.view.login.LoginViewModelState
import dagger.Lazy
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel  @Inject constructor(private val repository: Lazy<UserRepository>) :
    ViewModel() {

    val viewState = MutableLiveData(LoginViewModelState())

    init {
        val isLogged = repository.get().isUserLogged()
        viewModelScope.launch {
            viewState.value = viewState.value?.copy(loggedIn = isLogged )
            delay(5000)
            viewState.value = viewState.value?.copy(loggedIn = false )
//            repository.get().setUserLogged(false)
        }
    }

}