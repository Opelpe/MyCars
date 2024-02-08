package com.pepe.mycars.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pepe.mycars.app.data.domain.repository.DataRepository
import com.pepe.mycars.app.data.local.HistoryItemUiModel
import com.pepe.mycars.app.data.mapper.HistoryItemMapper
import com.pepe.mycars.app.data.model.HistoryItemModel
import com.pepe.mycars.app.utils.state.ItemModelState
import com.pepe.mycars.app.utils.state.view.HistoryItemViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class ItemHistoryViewModel @Inject
constructor(
    private val dataRepository: DataRepository
) : ViewModel() {

    private val _historyItemViewState: MutableLiveData<HistoryItemViewState> = MutableLiveData(HistoryItemViewState.Loading)
    val historyItemViewState: LiveData<HistoryItemViewState> = _historyItemViewState

        fun getListOfRefills() {
            dataRepository.getUserItems().onEach { state ->
                when(state){
                ItemModelState.Loading -> _historyItemViewState.postValue(HistoryItemViewState.Loading)

                is ItemModelState.Error -> _historyItemViewState.postValue(HistoryItemViewState.Error(state.exceptionMsg))

                is ItemModelState.Success -> {
                    if (state.model.isNotEmpty()) {

//                        val refillList = state.model.sortedByDescending { it.currMileage }
                        val list = HistoryItemMapper().mapToUiModel(state.model)

                        _historyItemViewState.postValue(HistoryItemViewState.Success(list, ""))
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
            _historyItemViewState.postValue(HistoryItemViewState.Error("Enter the necessary data!"))
        }else{

            dataRepository.addDataRefillItem(currMileage.toFloat(), fuelCost.toFloat(), fuelAmount.toFloat(), refillDate, notes ?: "").onEach { state ->
                when(state){
                    ItemModelState.Loading -> _historyItemViewState.postValue(HistoryItemViewState.Loading)

                    is ItemModelState.Error -> _historyItemViewState.postValue(HistoryItemViewState.Error(state.exceptionMsg))

                    is ItemModelState.Success -> {
                        val refillList = state.model.sortedByDescending { it.currMileage }
//                        formatFuelUsage(state.model)
                        val list = HistoryItemMapper().mapToUiModel(state.model)

                        _historyItemViewState.postValue(HistoryItemViewState.Success(list, "Saved!"))
                    }
                }
            }.launchIn(viewModelScope)
        }

    }

    fun startDataSync() {
        val emptyList = emptyList<HistoryItemUiModel>()
        _historyItemViewState.postValue(HistoryItemViewState.Success(emptyList,""))
    }

//    fun countRefillDetails(data: List<HistoryItemModel>): List<HistoryItemModel> {
//        val refillList = data.sortedByDescending { it.currMileage }
//        for (i in refillList.indices){
//            if (i+1 < refillList.size){
//                val newestMileage = refillList[i].currMileage  ?: 0.0
//                val lastMileage = refillList[i+1].currMileage ?: 0.0
//                val addedMileage = newestMileage - lastMileage
//                refillList[i].addedMileage = addedMileage
//            }
//        }
//        return refillList
//    }

//    fun updateRefillItem(itemId:String){
//        dataRepository.updateRefillItem(itemId).onEach{ state ->
//            when(state){
//                ItemModelState.Loading -> _historyItemViewState.postValue(HistoryItemViewState.Loading)
//
//                is ItemModelState.Error -> _historyItemViewState.postValue(HistoryItemViewState.Error(state.exceptionMsg))
//
//                is ItemModelState.Success -> {
//                    if (state.model.isNotEmpty()){
//                        val refillList = state.model.sortedByDescending { it.currMileage }
//                        _historyItemViewState.postValue(HistoryItemViewState.Success(state.model, "Saved!"))
//                    }
//                }
//            }
//        }.launchIn(viewModelScope)
//    }




}