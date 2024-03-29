package com.pepe.mycars.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pepe.mycars.app.data.domain.usecase.data.AddItemUseCase
import com.pepe.mycars.app.utils.state.ItemModelState
import com.pepe.mycars.app.utils.state.view.AddItemViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class RefillDialogViewModel @Inject constructor(
    private val addItemUseCase: AddItemUseCase
) : ViewModel() {

    private val _addingItemViewState: MutableLiveData<AddItemViewState> = MutableLiveData(AddItemViewState.Loading)
    val addingItemViewState: LiveData<AddItemViewState> = _addingItemViewState

    fun addRefill(
        currMileage: String?,
        fuelCost: String?,
        fuelAmount: String?,
        refillDate: String?,
        notes: String?
    ) {
        if (currMileage.isNullOrEmpty() || fuelCost.isNullOrEmpty() || fuelAmount.isNullOrEmpty() || refillDate.isNullOrEmpty()) {
            _addingItemViewState.postValue(AddItemViewState.Error("Enter the necessary data!"))
        } else {
            addItemUseCase.execute(
                AddItemUseCase.Param(
                    currMileage.toFloat(),
                    fuelCost.toFloat(),
                    fuelAmount.toFloat(),
                    refillDate,
                    notes ?: ""
                )
            ).onEach { state ->
                when (state) {
                    ItemModelState.Loading -> {
                        _addingItemViewState.postValue(AddItemViewState.Loading)
                    }

                    is ItemModelState.Error -> {
                        _addingItemViewState.postValue(AddItemViewState.Error(state.exceptionMsg))
                    }

                    is ItemModelState.Success -> {
                        _addingItemViewState.postValue(AddItemViewState.Success("Successfully added!"))
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    fun updateHistoryItem(
        itemID: String?,
        currMileage: String?,
        fuelAmount: String?,
        fuelCost: String?,
        refillDate: String?,
        notes: String?,
        fullTank: Boolean
    ) {

        if (currMileage.isNullOrEmpty() || fuelCost.isNullOrEmpty() || fuelAmount.isNullOrEmpty() || refillDate.isNullOrEmpty()) {
            _addingItemViewState.postValue(AddItemViewState.Error("Enter the necessary data!"))
        } else {
            addItemUseCase.execute(
                AddItemUseCase.Param(
                    currMileage.toFloat(),
                    fuelCost.toFloat(),
                    fuelAmount.toFloat(),
                    refillDate,
                    notes ?: ""
                )
            ).onEach { state ->
                when (state) {
                    ItemModelState.Loading -> {
                        _addingItemViewState.postValue(AddItemViewState.Loading)
                    }

                    is ItemModelState.Error -> {
                        _addingItemViewState.postValue(AddItemViewState.Error(state.exceptionMsg))
                    }

                    is ItemModelState.Success -> {
                        _addingItemViewState.postValue(AddItemViewState.Success("Item successfully edited!"))
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    fun dialogStart(itemEditMode: Boolean) {
        if (!itemEditMode) {
            _addingItemViewState.postValue(AddItemViewState.Error("Add new item"))
        } else {
            _addingItemViewState.postValue(AddItemViewState.Error("Edit item"))
        }
    }

    fun dialogEnd() {
        _addingItemViewState.postValue(AddItemViewState.Error(""))
    }


}