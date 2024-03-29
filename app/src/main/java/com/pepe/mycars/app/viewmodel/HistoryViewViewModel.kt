package com.pepe.mycars.app.viewmodel

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pepe.mycars.app.data.domain.repository.DataRepository
import com.pepe.mycars.app.data.domain.usecase.data.DeleteItemUseCase
import com.pepe.mycars.app.data.domain.usecase.data.GetRefillItemsUseCase
import com.pepe.mycars.app.data.mapper.HistoryItemMapper
import com.pepe.mycars.app.utils.RefillChangesLiveData
import com.pepe.mycars.app.utils.state.ItemModelState
import com.pepe.mycars.app.utils.state.view.HistoryItemViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class HistoryViewViewModel @Inject constructor(
    private val getRefillItemsUseCase: GetRefillItemsUseCase,
    private val deleteItemUseCase: DeleteItemUseCase,
    private val dataRepository: DataRepository
) : ViewModel() {

    private val _historyItemViewState: MutableLiveData<HistoryItemViewState> = MutableLiveData(HistoryItemViewState.Loading)
    val historyItemViewState: LiveData<HistoryItemViewState> = _historyItemViewState

    fun updateView() {
        getRefillItemsUseCase.execute().onEach { state ->
            when (state) {
                ItemModelState.Loading -> _historyItemViewState.postValue(HistoryItemViewState.Loading)

                is ItemModelState.Error -> _historyItemViewState.postValue(HistoryItemViewState.Error(state.exceptionMsg))

                is ItemModelState.Success -> {
                        val list = HistoryItemMapper().mapToHistoryUiModel(state.model)
                        _historyItemViewState.postValue(HistoryItemViewState.Success(list, ""))
                }
            }
        }.launchIn(viewModelScope)
    }

    fun deleteItem(itemId: String) {
        deleteItemUseCase.execute(DeleteItemUseCase.Param(itemId)).onEach { state ->
            when(state){
                ItemModelState.Loading -> _historyItemViewState.postValue(HistoryItemViewState.Loading)

                is ItemModelState.Error -> _historyItemViewState.postValue(HistoryItemViewState.Error(state.exceptionMsg))

                is ItemModelState.Success -> {
                    val list = HistoryItemMapper().mapToHistoryUiModel(state.model)
                    _historyItemViewState.postValue(HistoryItemViewState.Success(list.toList(), "Successfully removed!"))
                }
            }
        }.launchIn(viewModelScope)
    }

    fun observeRefillList(owner: LifecycleOwner) {
        RefillChangesLiveData(dataRepository.getRefillsCollectionReference()).observe(owner) {
            val list = HistoryItemMapper().mapToHistoryUiModel(it)
            _historyItemViewState.postValue(HistoryItemViewState.Success(list, ""))
        }
    }


}