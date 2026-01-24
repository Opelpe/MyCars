package com.pepe.mycars.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pepe.mycars.app.data.domain.usecase.auth.LogOutUseCase
import com.pepe.mycars.app.utils.state.view.UserViewState
import com.pepe.mycars.data.firebase.repo.IAuthRepository
import com.pepe.mycars.domain.repository.IUserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoggedInViewModel
    @Inject
    constructor(
        private val userRepository: IUserRepository,
        private val logOutUseCase: LogOutUseCase,
        private val authRepository: IAuthRepository,
    ) : ViewModel() {
        private val _userViewState: MutableLiveData<UserViewState> =
            MutableLiveData(UserViewState.Loading)
        val userViewState: LiveData<UserViewState> = _userViewState

        init {
            fetchUserData()
        }

        fun logOut() {
            logOutUseCase.execute()
        }

        fun fetchUserData() {
            viewModelScope.launch {
                combine(
                    flow = authRepository.validSessionFlow,
                    flow2 = userRepository.getSyncFirestoreUserData(),
                ) { isAuthenticated, user ->
                    val isLoggedIn = isAuthenticated && user != null
                    UserViewState.Success(
                        isLoggedIn = isLoggedIn,
                        autoLogin = user?.autoLogin ?: false,
                        successMsg = "",
                    )
                }
                    .distinctUntilChanged()
                    .onStart { _userViewState.postValue(UserViewState.Loading) }
                    .catch { e ->
                        _userViewState.postValue(
                            UserViewState.Error(
                                e.localizedMessage ?: "Unknown error",
                            ),
                        )
                    }
                    .collectLatest { state ->
                        _userViewState.postValue(state)
                    }
            }
        }
    }
