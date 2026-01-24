package com.pepe.mycars.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pepe.mycars.app.data.domain.usecase.data.GetRefillItemsUseCase
import com.pepe.mycars.app.data.mapper.MainViewModelMapper
import com.pepe.mycars.app.utils.FireStoreUserDocField
import com.pepe.mycars.app.utils.state.view.MainViewState
import com.pepe.mycars.domain.repository.IFuelDataRepository
import com.pepe.mycars.domain.repository.IUserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

@HiltViewModel
class MainViewModel
    @Inject
    constructor(
        private val fuelDataRepo: IFuelDataRepository,
        private val userRepository: IUserRepository,
        private val getRefillItemsUseCase: GetRefillItemsUseCase,
        private val mainViewModelMapper: MainViewModelMapper,
    ) : ViewModel() {
        private val _dataMainViewState: MutableLiveData<MainViewState> =
            MutableLiveData(MainViewState.Loading)
        val dataMainViewState: LiveData<MainViewState> = _dataMainViewState

        fun isUserAnonymous(): Boolean {
            val response = userRepository.getUserProviderType()
            return if (
                response == FireStoreUserDocField.ACCOUNT_PROVIDER_ANONYMOUS ||
                response == FireStoreUserDocField.ACCOUNT_PROVIDER_EMAIL ||
                response == FireStoreUserDocField.ACCOUNT_PROVIDER_GOOGLE
            ) {
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
            return if (response == FireStoreUserDocField.ACCOUNT_PROVIDER_ANONYMOUS ||
                response == FireStoreUserDocField.ACCOUNT_PROVIDER_EMAIL ||
                response == FireStoreUserDocField.ACCOUNT_PROVIDER_GOOGLE
            ) {
                if (response == FireStoreUserDocField.ACCOUNT_PROVIDER_ANONYMOUS) {
                    "Sign in & Synchronize data"
                } else {
                    "Your data is synchronized"
                }
            } else {
                response
            }
        }

        fun observeRefillList() {
            fuelDataRepo.observeUserItems()
                .map(mainViewModelMapper::mapToMainViewModel)
                .onEach {
                    _dataMainViewState.postValue(MainViewState.Success(it, ""))
                }.launchIn(viewModelScope)
        }

        fun getListOfRefills() {
            getRefillItemsUseCase.execute()
                .onStart { _dataMainViewState.postValue(MainViewState.Loading) }
                .map(mainViewModelMapper::mapToMainViewModel)
                .onEach { list ->
                    _dataMainViewState.value = MainViewState.Success(list, "")
                }
                .catch { e ->
                    _dataMainViewState.value =
                        MainViewState.Error(e.localizedMessage ?: "Unknown error")
                }
                .launchIn(viewModelScope)
        }
    }
