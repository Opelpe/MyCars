package com.pepe.mycars.app.viewmodel

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pepe.mycars.app.data.domain.repository.DataRepository
import com.pepe.mycars.app.data.domain.repository.UserRepository
import com.pepe.mycars.app.data.domain.usecase.data.GetRefillItemsUseCase
import com.pepe.mycars.app.data.mapper.MainViewModelMapper
import com.pepe.mycars.app.utils.FireStoreUserDocField
import com.pepe.mycars.app.utils.RefillChangesLiveData
import com.pepe.mycars.app.utils.state.ItemModelState
import com.pepe.mycars.app.utils.state.view.MainViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class MainViewModel
    @Inject
    constructor(
        private val userRepository: UserRepository,
        private val getRefillItemsUseCase: GetRefillItemsUseCase,
        private val dataRepository: DataRepository,
    ) : ViewModel() {
        private val _dataMainViewState: MutableLiveData<MainViewState> = MutableLiveData(MainViewState.Loading)
        val dataMainViewState: LiveData<MainViewState> = _dataMainViewState

        fun isUserAnonymous(): Boolean {
            val response = userRepository.getUserProviderType()
            return if (response == FireStoreUserDocField.ACCOUNT_PROVIDER_ANONYMOUS || response == FireStoreUserDocField.ACCOUNT_PROVIDER_EMAIL || response == FireStoreUserDocField.ACCOUNT_PROVIDER_GOOGLE) {
                response == FireStoreUserDocField.ACCOUNT_PROVIDER_ANONYMOUS
            } else {
                true
            }
        }

        fun actionSynchronize() {
            _dataMainViewState.postValue(
                MainViewState.Error(
                    getActionSynchronizeResponse(
                        userRepository.getUserProviderType(),
                    ),
                ),
            )
        }

        private fun getActionSynchronizeResponse(response: String): String {
            return if (response == FireStoreUserDocField.ACCOUNT_PROVIDER_ANONYMOUS || response == FireStoreUserDocField.ACCOUNT_PROVIDER_EMAIL || response == FireStoreUserDocField.ACCOUNT_PROVIDER_GOOGLE) {
                if (response == FireStoreUserDocField.ACCOUNT_PROVIDER_ANONYMOUS) "Sign in & Synchronize data" else "Your data is synchronized"
            } else {
                response
            }
        }

        fun observeRefillList(owner: LifecycleOwner) {
            RefillChangesLiveData(dataRepository.getRefillsCollectionReference()).observe(owner) {
                val newModel = MainViewModelMapper().mapToMainViewModel(it)
                _dataMainViewState.postValue(MainViewState.Success(newModel, ""))
            }
        }

        fun getListOfRefills() {
            getRefillItemsUseCase.execute().onEach { state ->
                when (state) {
                    ItemModelState.Loading -> _dataMainViewState.postValue(MainViewState.Loading)

                    is ItemModelState.Error -> _dataMainViewState.postValue(MainViewState.Error(state.exceptionMsg))

                    is ItemModelState.Success -> {
                        val newModel = MainViewModelMapper().mapToMainViewModel(state.model)
                        _dataMainViewState.postValue(MainViewState.Success(newModel, ""))
                    }
                }
            }.launchIn(viewModelScope)
        }
    }
