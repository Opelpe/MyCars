package com.pepe.mycars.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pepe.mycars.app.data.domain.repository.DataRepository
import com.pepe.mycars.app.data.local.HistoryItemUiModel
import com.pepe.mycars.app.data.mapper.HistoryItemMapper
import com.pepe.mycars.app.utils.state.ItemModelState
import com.pepe.mycars.app.utils.state.view.HistoryItemViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class HistoryViewViewModel @Inject
constructor(
    private val dataRepository: DataRepository
) : ViewModel() {

    private val _historyItemViewState: MutableLiveData<HistoryItemViewState> = MutableLiveData(HistoryItemViewState.Loading)
    val historyItemViewState: LiveData<HistoryItemViewState> = _historyItemViewState

        fun getListOfRefills() {
            dataRepository.getUserItems().onEach { state ->
                when(state){
                ItemModelState.Loading -> _historyItemViewState.postValue(HistoryItemViewState.Loading)

                is ItemModelState.Error -> _historyItemViewState.postValue(HistoryItemViewState.Error(state.exceptionMsg, HistoryOperations.NONE))

                is ItemModelState.Success -> {
                    if (state.model.isNotEmpty()) {
                        val list = HistoryItemMapper().mapToHistoryUiModel(state.model)
                        _historyItemViewState.postValue(HistoryItemViewState.Success(list, "", HistoryOperations.NONE))
                    }
                }
        }
        }.launchIn(viewModelScope)
    }

    fun addRefill(
        currMileage: String?,
        fuelCost: String?,
        fuelAmount: String?,
        refillDate: String?,
        notes: String?
    ) {

        if (currMileage.isNullOrEmpty() || fuelCost.isNullOrEmpty() || fuelAmount.isNullOrEmpty() || refillDate.isNullOrEmpty()){
            _historyItemViewState.postValue(HistoryItemViewState.Error("Enter the necessary data!", HistoryOperations.NONE))
        }else{

            dataRepository.addRefillItem(currMileage.toFloat(), fuelCost.toFloat(), fuelAmount.toFloat(), refillDate, notes ?: "").onEach { state ->
                when(state){
                    ItemModelState.Loading -> _historyItemViewState.postValue(HistoryItemViewState.Loading)

                    is ItemModelState.Error -> _historyItemViewState.postValue(HistoryItemViewState.Error(state.exceptionMsg, HistoryOperations.NONE))

                    is ItemModelState.Success -> {
                        val list = HistoryItemMapper().mapToHistoryUiModel(state.model)
                        _historyItemViewState.postValue(HistoryItemViewState.Success(list, "", HistoryOperations.ADDED))
                    }
                }
            }.launchIn(viewModelScope)
        }

    }

    fun startDataSync() {
        val emptyList = emptyList<HistoryItemUiModel>()
        _historyItemViewState.postValue(HistoryItemViewState.Success(emptyList, "", HistoryOperations.START_DATA))
    }

    fun deleteItem(itemId: String) {
        dataRepository.deleteRefillItem(itemId).onEach { state ->
            when(state){
                ItemModelState.Loading -> _historyItemViewState.postValue(HistoryItemViewState.Loading)

                is ItemModelState.Error -> _historyItemViewState.postValue(HistoryItemViewState.Error(state.exceptionMsg, HistoryOperations.NONE))

                is ItemModelState.Success -> {
                    val list = HistoryItemMapper().mapToHistoryUiModel(state.model)
                    _historyItemViewState.postValue(HistoryItemViewState.Success(list, "", HistoryOperations.REMOVED))
                }
            }
        }.launchIn(viewModelScope)
    }

    fun displayDeletionConfirmation(itemId: String) {
        _historyItemViewState.postValue(HistoryItemViewState.Error(itemId, HistoryOperations.DISPLAY_CONFIRMATION_DIALOG))
    }

}

enum class HistoryOperations {
    NONE,
    DISPLAY_CONFIRMATION_DIALOG,
    START_DATA,
    REMOVED,
    ADDED

}