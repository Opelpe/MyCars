package com.pepe.mycars.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pepe.mycars.app.utils.state.view.RefillItemViewState
import com.pepe.mycars.domain.repository.IFuelDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

@HiltViewModel
class RefillDialogViewModel
    @Inject
    constructor(
        private val fuelDataRepo: IFuelDataRepository,
    ) : ViewModel() {
        private val _refillItemViewState: MutableStateFlow<RefillItemViewState> =
            MutableStateFlow(RefillItemViewState.Idle)
        val refillItemViewState: StateFlow<RefillItemViewState> = _refillItemViewState.asStateFlow()

        fun addRefill(
            currMileage: String?,
            fuelCost: String?,
            fuelAmount: String?,
            refillDate: String?,
            notes: String?,
            fullTank: Boolean,
        ) {
            if (currMileage.isNullOrEmpty() || fuelCost.isNullOrEmpty() || fuelAmount.isNullOrEmpty() || refillDate.isNullOrEmpty()) {
                _refillItemViewState.value = RefillItemViewState.Error("Enter the necessary data!")
                return
            }

            fuelDataRepo.addRefillItem(
                currMileage.toFloat(),
                fuelCost.toFloat(),
                fuelAmount.toFloat(),
                refillDate,
                notes ?: "",
                fullTank,
            )
                .onStart { _refillItemViewState.value = RefillItemViewState.Loading }
                .onEach {
                    _refillItemViewState.value =
                        RefillItemViewState.Success(
                            null,
                            RefillOperations.ADDED,
                            "Successfully added!",
                        )
                }
                .catch { e ->
                    _refillItemViewState.value =
                        RefillItemViewState.Error(e.localizedMessage ?: "Unknown error")
                }
                .launchIn(viewModelScope)
        }

        fun updateHistoryItem(
            itemID: String,
            currMileage: String?,
            fuelAmount: String?,
            fuelCost: String?,
            refillDate: String?,
            notes: String?,
            fullTank: Boolean,
        ) {
            if (currMileage.isNullOrEmpty() || fuelCost.isNullOrEmpty() || fuelAmount.isNullOrEmpty() || refillDate.isNullOrEmpty()) {
                _refillItemViewState.value = RefillItemViewState.Error("Enter the necessary data!")
                return
            }

            fuelDataRepo.updateItem(
                itemID = itemID,
                currMileage = currMileage.toFloat(),
                fuelAmount = fuelAmount.toFloat(),
                fuelCost = fuelCost.toFloat(),
                refillDate = refillDate,
                notes = notes ?: "",
                fullTank = fullTank,
            )
                .onStart { _refillItemViewState.value = RefillItemViewState.Loading }
                .onEach {
                    _refillItemViewState.value =
                        RefillItemViewState.Success(
                            null,
                            RefillOperations.UPDATED,
                            "Item successfully edited!",
                        )
                }
                .catch { e ->
                    _refillItemViewState.value =
                        RefillItemViewState.Error(e.localizedMessage ?: "Unknown error")
                }
                .launchIn(viewModelScope)
        }

        fun getItemById(editItemID: String) {
            fuelDataRepo.getItemById(editItemID)
                .onStart { _refillItemViewState.value = RefillItemViewState.Loading }
                .catch { e ->
                    _refillItemViewState.value =
                        RefillItemViewState.Error(e.localizedMessage ?: "Unknown error")
                }
                .onEach {
                    _refillItemViewState.value =
                        RefillItemViewState.Success(
                            it,
                            null,
                            "",
                        )
                }
                .launchIn(viewModelScope)
        }

        fun resetState() {
            _refillItemViewState.value = RefillItemViewState.Idle
        }
    }

enum class RefillOperations {
    ADDED,
    UPDATED,
}
