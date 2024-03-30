package com.pepe.mycars.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pepe.mycars.app.data.domain.repository.DataRepository
import com.pepe.mycars.app.utils.state.ItemModelState
import com.pepe.mycars.app.utils.state.view.RefillItemViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class RefillDialogViewModel @Inject constructor(
    private val dataRepository: DataRepository
) : ViewModel() {

    private val _refillItemViewState: MutableLiveData<RefillItemViewState> = MutableLiveData(RefillItemViewState.Loading)
    val refillItemViewState: LiveData<RefillItemViewState> = _refillItemViewState

    fun addRefill(
        currMileage: String?, fuelCost: String?, fuelAmount: String?, refillDate: String?, notes: String?, fullTank: Boolean
    ) {
        if (currMileage.isNullOrEmpty() || fuelCost.isNullOrEmpty() || fuelAmount.isNullOrEmpty() || refillDate.isNullOrEmpty()) {
            _refillItemViewState.postValue(RefillItemViewState.Error("Enter the necessary data!"))
        } else {
            dataRepository.addRefillItem(
                currMileage.toFloat(), fuelCost.toFloat(), fuelAmount.toFloat(), refillDate, notes ?: "", fullTank
            ).onEach { state ->
                when (state) {
                    ItemModelState.Loading -> {
                        _refillItemViewState.postValue(RefillItemViewState.Loading)
                    }

                    is ItemModelState.Error -> {
                        _refillItemViewState.postValue(RefillItemViewState.Error(state.exceptionMsg))
                    }

                    is ItemModelState.Success -> {
                        _refillItemViewState.postValue(RefillItemViewState.Success(null, RefillOperations.ADDED, "Successfully added!"))
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    fun updateHistoryItem(
        itemID: String, currMileage: String?, fuelAmount: String?, fuelCost: String?, refillDate: String?, notes: String?, fullTank: Boolean
    ) {

        if (currMileage.isNullOrEmpty() || fuelCost.isNullOrEmpty() || fuelAmount.isNullOrEmpty() || refillDate.isNullOrEmpty()) {
            _refillItemViewState.postValue(RefillItemViewState.Error("Enter the necessary data!"))
        } else {

            dataRepository.updateItem(
                itemID, currMileage.toFloat(), fuelAmount.toFloat(), fuelCost.toFloat(), refillDate, notes ?: "", fullTank
            ).onEach { state ->
                val value = when (state) {
                    ItemModelState.Loading -> RefillItemViewState.Loading

                    is ItemModelState.Error -> RefillItemViewState.Error(state.exceptionMsg)

                    is ItemModelState.Success -> RefillItemViewState.Success(null, RefillOperations.UPDATED, "Item successfully edited!")
                }
                _refillItemViewState.postValue(value)
            }.launchIn(viewModelScope)
        }
    }


    fun getItemById(editItemID: String) {
        dataRepository.getItemById(editItemID).onEach { state ->
            when (state) {
                ItemModelState.Loading -> {
                    _refillItemViewState.postValue(RefillItemViewState.Loading)
                }

                is ItemModelState.Error -> {
                    _refillItemViewState.postValue(RefillItemViewState.Error(state.exceptionMsg))
                }

                is ItemModelState.Success -> {
                    if (state.model.isNotEmpty()) {
                        _refillItemViewState.postValue(RefillItemViewState.Success(state.model[0], null, ""))
                    } else {
                        _refillItemViewState.postValue(RefillItemViewState.Error("Item not found"))
                    }
                }
            }

        }.launchIn(viewModelScope)
    }

    fun dialogStartEnd() {
        _refillItemViewState.postValue(RefillItemViewState.Error(""))
    }

}

enum class RefillOperations {
    ADDED, UPDATED
}