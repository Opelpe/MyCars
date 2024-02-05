package com.pepe.mycars.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pepe.mycars.app.data.domain.repository.DataRepository
import com.pepe.mycars.app.utils.state.ItemModelState
import com.pepe.mycars.app.utils.state.view.DataViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class ItemHistoryViewModel @Inject
constructor(
    private val dataRepository: DataRepository
) : ViewModel() {

    private val _dataViewState: MutableLiveData<DataViewState> = MutableLiveData(DataViewState.Loading)
    val dataViewState: LiveData<DataViewState> = _dataViewState



//    fun getListOfRefills(): List<ItemRefillModel> {
        fun getListOfRefills() {


            dataRepository.getUserItems().onEach {
            when(it){
                ItemModelState.Loading -> _dataViewState.postValue(DataViewState.Loading)

                is ItemModelState.Error -> _dataViewState.postValue(DataViewState.Error(it.exceptionMsg))

                is ItemModelState.Success -> {
                    if (it.model.isNotEmpty()) {
                        _dataViewState.postValue(DataViewState.Success("${it.model.size} refill"))
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
            _dataViewState.postValue(DataViewState.Error("Enter the necessary data!"))
        }else{
            dataRepository.addDataRefillItem(currMileage, fuelCost, fuelAmount, refillDate, notes ?: "").onEach {
                when(it){
                    ItemModelState.Loading -> _dataViewState.postValue(DataViewState.Loading)

                    is ItemModelState.Error -> _dataViewState.postValue(DataViewState.Error(it.exceptionMsg))

                    is ItemModelState.Success -> {
                        if (it.model.isNotEmpty()){
                            _dataViewState.postValue(DataViewState.Success("Saved!"))
                        }
                    }
                }
            }.launchIn(viewModelScope)
        }

    }

    fun startDataSync() {
        _dataViewState.postValue(DataViewState.Success(""))
    }
}