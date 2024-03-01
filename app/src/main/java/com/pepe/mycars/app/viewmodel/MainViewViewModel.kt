package com.pepe.mycars.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pepe.mycars.app.data.domain.repository.UserRepository
import com.pepe.mycars.app.data.domain.usecase.GetUserDataUseCase
import com.pepe.mycars.app.data.mapper.MainViewModelMapper
import com.pepe.mycars.app.data.model.HistoryItemModel
import com.pepe.mycars.app.utils.FireStoreUserDocField
import com.pepe.mycars.app.utils.state.ItemModelState
import com.pepe.mycars.app.utils.state.UserModelState
import com.pepe.mycars.app.utils.state.view.MainViewState
import com.pepe.mycars.app.utils.state.view.UserViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class MainViewViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val getUserDataUseCase: GetUserDataUseCase
) : ViewModel (){

    private val _userMainViewState: MutableLiveData<UserViewState> =
        MutableLiveData(UserViewState.Loading)
    val userMainViewState: LiveData<UserViewState> = _userMainViewState

    private val _dataMainViewState: MutableLiveData<MainViewState> =
        MutableLiveData(MainViewState.Loading)
    val dataMainViewState: LiveData<MainViewState> = _dataMainViewState

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

 fun getListOfRefills() {
     getUserDataUseCase.execute().onEach { state ->
         when(state){
             ItemModelState.Loading -> _dataMainViewState.postValue(MainViewState.Loading)

             is ItemModelState.Error -> _dataMainViewState.postValue(MainViewState.Error(state.exceptionMsg))

             is ItemModelState.Success -> {
                 if (state.model.isNotEmpty()) {
                     val newModel = MainViewModelMapper().mapToMainViewModel(state.model)
                     _dataMainViewState.postValue(MainViewState.Success(newModel, ""))
                 }
             }
         }
     }.launchIn(viewModelScope)
}

    fun addingItem() {
        _dataMainViewState.postValue(MainViewState.Loading)
    }

    fun addingItemError(exceptionMsg: String) {
        _dataMainViewState.postValue(MainViewState.Error(exceptionMsg))
    }

    fun addingItemSuccess(model: List<HistoryItemModel>, message: String) {
        val newModel = MainViewModelMapper().mapToMainViewModel(model)
        _dataMainViewState.postValue(MainViewState.Success(newModel, message))
    }
}