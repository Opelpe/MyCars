package com.pepe.mycars.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pepe.mycars.app.data.domain.usecase.AddItemUseCase
import com.pepe.mycars.app.utils.state.ItemModelState
import com.pepe.mycars.app.utils.state.view.HistoryItemViewState
import com.pepe.mycars.app.utils.state.view.MainViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class RefillDialogViewModel @Inject constructor(private val historyViewModel: HistoryViewViewModel, private val mainViewViewModel: MainViewViewModel, private val addItemUseCase: AddItemUseCase,) : ViewModel() {

    fun addRefill(
        currMileage: String?,
        fuelCost: String?,
        fuelAmount: String?,
        refillDate: String?,
        notes: String?
    ) {
        if (currMileage.isNullOrEmpty() || fuelCost.isNullOrEmpty() || fuelAmount.isNullOrEmpty() || refillDate.isNullOrEmpty()){
            historyViewModel.addingItemError("Enter the necessary data!", HistoryOperations.NONE)
            mainViewViewModel.addingItemError("Enter the necessary data!")
        }else{
            addItemUseCase.execute(AddItemUseCase.Param(currMileage.toFloat(), fuelCost.toFloat(), fuelAmount.toFloat(), refillDate, notes ?: "")).onEach { state ->
                when(state){
                    ItemModelState.Loading -> {
                        historyViewModel.addingItem()
                        mainViewViewModel.addingItem()
                    }

                    is ItemModelState.Error -> {
                        historyViewModel.addingItemError(state.exceptionMsg, HistoryOperations.NONE)
                        mainViewViewModel.addingItemError(state.exceptionMsg)
                    }

                    is ItemModelState.Success -> {
                        historyViewModel.addingItemSuccess(state.model, "", HistoryOperations.ADDED)
                        mainViewViewModel.addingItemSuccess(state.model, "Successfully added!")

                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    fun refreshRefillList() {
        historyViewModel.updateView()
        mainViewViewModel.getListOfRefills()
    }

    fun getMainViewState(): LiveData<MainViewState> {
        return mainViewViewModel.dataMainViewState
    }

    fun getHistoryItemViewState(): LiveData<HistoryItemViewState> {
        return historyViewModel.historyItemViewState
    }
}