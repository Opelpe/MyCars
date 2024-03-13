package com.pepe.mycars.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pepe.mycars.app.data.domain.usecase.data.DeleteItemUseCase
import com.pepe.mycars.app.data.domain.usecase.data.GetUserItemsUseCase
import com.pepe.mycars.app.data.mapper.HistoryItemMapper
import com.pepe.mycars.app.data.model.HistoryItemModel
import com.pepe.mycars.app.utils.state.ItemModelState
import com.pepe.mycars.app.utils.state.view.HistoryItemViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class HistoryViewViewModel @Inject constructor(
    private val getUserItemsUseCase: GetUserItemsUseCase,
    private val deleteItemUseCase: DeleteItemUseCase
) : ViewModel() {

    private val _historyItemViewState: MutableLiveData<HistoryItemViewState> = MutableLiveData(HistoryItemViewState.Loading)
    val historyItemViewState: LiveData<HistoryItemViewState> = _historyItemViewState

    fun updateView() {
        getUserItemsUseCase.execute().onEach { state ->
            when (state) {
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

    fun deleteItem(itemId: String) {
        deleteItemUseCase.execute(DeleteItemUseCase.Param(itemId)).onEach { state ->
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

    fun addingItem() {
        _historyItemViewState.postValue(HistoryItemViewState.Loading)
    }

    fun addingItemError(exceptionMsg: String, operations: HistoryOperations) {
        _historyItemViewState.postValue(HistoryItemViewState.Error(exceptionMsg, operations))
    }

    fun addingItemSuccess(model: List<HistoryItemModel>, message: String, operations: HistoryOperations) {
        val list = HistoryItemMapper().mapToHistoryUiModel(model)
        _historyItemViewState.postValue(HistoryItemViewState.Success(list, message, operations))
    }


}

enum class HistoryOperations {
    NONE,
    REMOVED,
    ADDED

}