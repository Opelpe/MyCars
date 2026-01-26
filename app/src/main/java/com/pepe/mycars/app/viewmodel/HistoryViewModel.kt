package com.pepe.mycars.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pepe.mycars.app.data.mapper.HistoryItemMapper
import com.pepe.mycars.app.utils.state.view.HistoryItemViewState
import com.pepe.mycars.domain.repository.IFuelDataRepository
import com.pepe.mycars.domain.usecase.fuel.DeleteItemUseCase
import com.pepe.mycars.domain.usecase.fuel.GetRefillItemsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel
    @Inject
    constructor(
        private val fuelDataRepo: IFuelDataRepository,
        private val historyItemMapper: HistoryItemMapper,
        private val getRefillItemsUseCase: GetRefillItemsUseCase,
        private val deleteItemUseCase: DeleteItemUseCase,
    ) : ViewModel() {
        private val _historyItemViewState: MutableLiveData<HistoryItemViewState> =
            MutableLiveData(HistoryItemViewState.Loading)
        val historyItemViewState: LiveData<HistoryItemViewState> = _historyItemViewState

        fun updateView() {
            getRefillItemsUseCase.execute()
                .onStart { _historyItemViewState.value = HistoryItemViewState.Loading }
                .map(historyItemMapper::mapToHistoryUiModel)
                .onEach { list ->
                    _historyItemViewState.value = HistoryItemViewState.Success(list, "")
                }
                .catch { e ->
                    _historyItemViewState.value =
                        HistoryItemViewState.Error(e.localizedMessage ?: "Unknown error")
                }
                .launchIn(viewModelScope)
        }

        fun deleteItem(itemId: String) {
            deleteItemUseCase.execute(DeleteItemUseCase.Param(itemId))
                .onStart { _historyItemViewState.value = HistoryItemViewState.Loading }
                .map(historyItemMapper::mapToHistoryUiModel)
                .onEach { list ->
                    _historyItemViewState.value =
                        HistoryItemViewState.Success(list, "Successfully removed!")
                }
                .catch { e ->
                    _historyItemViewState.value =
                        HistoryItemViewState.Error(e.localizedMessage ?: "Unknown error")
                }
                .launchIn(viewModelScope)
        }

        fun observeRefillList() {
            fuelDataRepo.observeUserItems()
                .map(historyItemMapper::mapToHistoryUiModel)
                .onEach { list ->
                    _historyItemViewState.postValue(HistoryItemViewState.Success(list, ""))
                }.launchIn(viewModelScope)
        }
    }
