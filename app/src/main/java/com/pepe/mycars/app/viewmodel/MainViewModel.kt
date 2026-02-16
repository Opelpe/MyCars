package com.pepe.mycars.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pepe.mycars.app.data.mapper.MainViewModelMapper
import com.pepe.mycars.app.utils.FireStoreUserDocField.ACCOUNT_PROVIDER_ANONYMOUS
import com.pepe.mycars.app.utils.FireStoreUserDocField.ACCOUNT_PROVIDER_EMAIL
import com.pepe.mycars.app.utils.FireStoreUserDocField.ACCOUNT_PROVIDER_GOOGLE
import com.pepe.mycars.app.utils.state.view.MainViewState
import com.pepe.mycars.app.utils.state.view.MainViewState.Error
import com.pepe.mycars.app.utils.state.view.MainViewState.Loading
import com.pepe.mycars.app.utils.state.view.MainViewState.Success
import com.pepe.mycars.domain.manager.INetworkManager
import com.pepe.mycars.domain.model.FuelDataInfo
import com.pepe.mycars.domain.repository.IFuelDataRepository
import com.pepe.mycars.domain.repository.IUserRepository
import com.pepe.mycars.domain.usecase.fuel.GetRefillItemsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
        private val mapper: MainViewModelMapper,
        networkManager: INetworkManager,
    ) : ViewModel() {
        private val _dataMainViewState = MutableStateFlow<MainViewState>(Loading)
        val dataMainViewState: StateFlow<MainViewState> = _dataMainViewState.asStateFlow()

        val isConnected: Flow<Boolean> = networkManager.isConnected

        init {
            getListOfRefills()
            observeRefillList()
        }

        fun isUserAnonymous(): Boolean = userRepository.getUserProviderType() !in REGISTERED_PROVIDERS

        fun actionSynchronize() {
            val message =
                when (val provider = userRepository.getUserProviderType()) {
                    ACCOUNT_PROVIDER_ANONYMOUS -> "Sign in & Synchronize data"
                    in REGISTERED_PROVIDERS -> "Your data is synchronized"
                    else -> provider
                }
            _dataMainViewState.value = Error(message)
        }

        fun observeRefillList() {
            collectData(fuelDataRepo.observeUserItems())
        }

        fun getListOfRefills() {
            collectData(getRefillItemsUseCase.execute(), showLoading = true)
        }

        private fun collectData(
            flow: Flow<List<FuelDataInfo>>,
            showLoading: Boolean = false,
        ) {
            flow.onStart { if (showLoading) _dataMainViewState.value = Loading }
                .map { Success(mapper.mapToMainViewModel(it), "") }
                .onEach { _dataMainViewState.value = it }
                .catch { _dataMainViewState.value = Error(it.localizedMessage ?: "Unknown error") }
                .launchIn(viewModelScope)
        }

        companion object {
            private val REGISTERED_PROVIDERS = listOf(ACCOUNT_PROVIDER_EMAIL, ACCOUNT_PROVIDER_GOOGLE)
        }
    }
