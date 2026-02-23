package com.pepe.mycars.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pepe.mycars.app.utils.state.view.UserViewState
import com.pepe.mycars.app.utils.toUiText
import com.pepe.mycars.domain.repository.IAuthRepository
import com.pepe.mycars.domain.repository.IUserRepository
import com.pepe.mycars.domain.usecase.auth.LogOutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class LoggedInViewModel
    @Inject
    constructor(
        private val logOutUseCase: LogOutUseCase,
        userRepository: IUserRepository,
        authRepository: IAuthRepository,
    ) : ViewModel() {
        val userViewState: StateFlow<UserViewState> =
            combine(
                flow = authRepository.validSessionFlow,
                flow2 = userRepository.getSyncFirestoreUserData(),
            ) { isAuthenticated, user ->
                UserViewState.Success(isLoggedIn = isAuthenticated && user != null) as UserViewState
            }
                .onStart { emit(UserViewState.Loading) }
                .distinctUntilChanged()
                .catch { e ->
                    emit(UserViewState.Error(e.toUiText()))
                }
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(FLOW_STOP_TIMEOUT_MS),
                    initialValue = UserViewState.Loading,
                )

        fun logOut() {
            logOutUseCase.execute()
        }

        companion object {
            private const val FLOW_STOP_TIMEOUT_MS = 5000L
        }
    }
