package com.pepe.mycars.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pepe.mycars.app.utils.state.view.RefillItemViewState
import com.pepe.mycars.domain.repository.IFuelDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
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
        private val _refillItemViewState: MutableLiveData<RefillItemViewState> = MutableLiveData(RefillItemViewState.Loading)
        val refillItemViewState: LiveData<RefillItemViewState> = _refillItemViewState

        fun addRefill(
            currMileage: String?,
            fuelCost: String?,
            fuelAmount: String?,
            refillDate: String?,
            notes: String?,
            fullTank: Boolean,
        ) {
            if (currMileage.isNullOrEmpty() || fuelCost.isNullOrEmpty() || fuelAmount.isNullOrEmpty() || refillDate.isNullOrEmpty()) {
                _refillItemViewState.postValue(RefillItemViewState.Error("Enter the necessary data!"))
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
                .onStart { _refillItemViewState.postValue(RefillItemViewState.Loading) }
                .onEach {
                    _refillItemViewState.postValue(
                        RefillItemViewState
                            .Success(
                                null,
                                RefillOperations.ADDED,
                                "Successfully added!",
                            ),
                    )
                }
                .catch { e -> _refillItemViewState.postValue(RefillItemViewState.Error(e.localizedMessage ?: "Unknown error")) }
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
                _refillItemViewState.postValue(RefillItemViewState.Error("Enter the necessary data!"))
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
                .onStart { _refillItemViewState.postValue(RefillItemViewState.Loading) }
                .catch { e -> _refillItemViewState.postValue(RefillItemViewState.Error(e.localizedMessage ?: "Unknown error")) }
                .onEach {
                    _refillItemViewState.postValue(
                        RefillItemViewState
                            .Success(
                                null,
                                RefillOperations.UPDATED,
                                "Item successfully edited!",
                            ),
                    )
                }
                .launchIn(viewModelScope)
        }

        fun getItemById(editItemID: String) {
            fuelDataRepo.getItemById(editItemID)
                .onStart { _refillItemViewState.postValue(RefillItemViewState.Loading) }
                .catch { e -> _refillItemViewState.postValue(RefillItemViewState.Error(e.localizedMessage ?: "Unknown error")) }
                .onEach {
                    _refillItemViewState.postValue(
                        RefillItemViewState
                            .Success(
                                it,
                                null,
                                "",
                            ),
                    )
                }
                .launchIn(viewModelScope)
        }

        fun dialogStartEnd() {
            _refillItemViewState.postValue(RefillItemViewState.Error(""))
        }
    }

enum class RefillOperations {
    ADDED,
    UPDATED,
}
