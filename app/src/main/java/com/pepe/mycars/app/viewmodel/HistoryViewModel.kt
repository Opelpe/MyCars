package com.pepe.mycars.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pepe.mycars.R
import com.pepe.mycars.app.data.mapper.HistoryItemMapper
import com.pepe.mycars.app.utils.UiText
import com.pepe.mycars.app.utils.state.view.HistoryItemViewState
import com.pepe.mycars.app.utils.toUiText
import com.pepe.mycars.domain.repository.IFuelDataRepository
import com.pepe.mycars.domain.usecase.fuel.DeleteItemUseCase
import com.pepe.mycars.domain.usecase.fuel.GetRefillItemsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
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
class HistoryViewModel
    @Inject
    constructor(
        private val fuelDataRepo: IFuelDataRepository,
        private val historyItemMapper: HistoryItemMapper,
        private val getRefillItemsUseCase: GetRefillItemsUseCase,
        private val deleteItemUseCase: DeleteItemUseCase,
    ) : ViewModel() {
        private val _historyItemViewState: MutableStateFlow<HistoryItemViewState> =
            MutableStateFlow(HistoryItemViewState.Loading)
        val historyItemViewState: StateFlow<HistoryItemViewState> = _historyItemViewState.asStateFlow()

        fun updateView() {
            getRefillItemsUseCase.execute()
                .onStart { _historyItemViewState.value = HistoryItemViewState.Loading }
                .map(historyItemMapper::mapToHistoryUiModel)
                .onEach { list ->
                    _historyItemViewState.value = HistoryItemViewState.Success(list)
                }
                .catch { e ->
                    _historyItemViewState.value = HistoryItemViewState.Error(e.toUiText())
                }
                .launchIn(viewModelScope)
        }

        fun deleteItem(itemId: String) {
            deleteItemUseCase.execute(DeleteItemUseCase.Param(itemId))
                .onStart { _historyItemViewState.value = HistoryItemViewState.Loading }
                .map(historyItemMapper::mapToHistoryUiModel)
                .onEach { list ->
                    _historyItemViewState.value =
                        HistoryItemViewState.Success(list, UiText.StringResource(R.string.msg_item_deleted))
                }
                .catch { e ->
                    _historyItemViewState.value = HistoryItemViewState.Error(e.toUiText())
                }
                .launchIn(viewModelScope)
        }

        fun observeRefillList() {
            fuelDataRepo.observeUserItems()
                .map(historyItemMapper::mapToHistoryUiModel)
                .onEach { list ->
                    _historyItemViewState.value = HistoryItemViewState.Success(list)
                }.launchIn(viewModelScope)
        }
    }
