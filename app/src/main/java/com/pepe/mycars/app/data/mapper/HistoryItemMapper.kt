package com.pepe.mycars.app.data.mapper

import com.pepe.mycars.app.data.local.HistoryItemUiModel
import com.pepe.mycars.domain.model.FuelDataInfo
import javax.inject.Inject

class HistoryItemMapper
    @Inject
    constructor() {
        fun mapToHistoryUiModel(model: List<FuelDataInfo>): List<HistoryItemUiModel> {
            val uiList = mutableListOf<HistoryItemUiModel>()
            for (i in model.indices) {
                if (i + 1 < model.size) {
                    val date = formatDateToString(model[i].refillDate)
                    val currentMileage = model[i].currMileage ?: 0f
                    val lastMileage = model[i + 1].currMileage ?: 0f
                    val addedMileage = currentMileage - lastMileage

                    val fCurrentMileage = formatMileage(currentMileage)
                    val fFuelAmount = formatFuelAmount(model[i].fuelAmount!!)
                    val fAddedMileage = formatAddedMileage(addedMileage)
                    val fFuelUsage = if (model[i].fullTank) formatFuelUsage((model[i].fuelAmount?.times(100))?.div(addedMileage)!!) else ""
                    val fFuelCost = formatFuelCost(model[i].fuelPrice!!, model[i].fuelAmount!!)
                    val uiItem =
                        HistoryItemUiModel(
                            model[i].itemId!!,
                            fCurrentMileage,
                            fFuelCost,
                            fFuelAmount,
                            date,
                            model[i].notes,
                            fAddedMileage,
                            fFuelUsage,
                        )
                    uiList.add(uiItem)
                } else {
                    val date = formatDateToString(model[i].refillDate)
                    val fCurrentMileage = formatMileage(model[i].currMileage!!)
                    val fFuelAmount = formatFuelAmount(model[i].fuelAmount!!)
                    val fFuelCost = formatFuelCost(model[i].fuelPrice!!, model[i].fuelAmount!!)
                    val uiItem =
                        HistoryItemUiModel(
                            model[i].itemId!!,
                            fCurrentMileage,
                            fFuelCost,
                            fFuelAmount,
                            date,
                            model[i].notes,
                            "",
                            "",
                        )
                    uiList.add(uiItem)
                }
            }
            return uiList
        }

        private fun formatDateToString(refillDate: String): String {
            return refillDate
        }

        private fun formatFuelCost(
            fp: Float,
            fa: Float,
        ): String {
            val fuelCost = fp * fa
            return if (fuelCost > 9999) {
                "+9999"
            } else {
                if (fuelCost > 999) {
                    String.format("%.0f", fuelCost)
                } else {
                    String.format("%.2f", fuelCost)
                }
            }
        }

        private fun formatAddedMileage(diff: Float): String {
            return if (diff > 99999) {
                "+  --- ---"
            } else {
                if (diff > 999) {
                    "+" + String.format("%.0f", diff)
                } else {
                    if (diff < 0) {
                        "---"
                    } else {
                        "+" + String.format("%.1f", diff)
                    }
                }
            }
        }

        private fun formatFuelUsage(fuelUsage: Float): String {
            return if (fuelUsage > 9999) {
                "# , #"
            } else {
                if (fuelUsage > 999) {
                    String.format("%.0f", fuelUsage)
                } else {
                    if (fuelUsage > 99) {
                        String.format("%.1f", fuelUsage)
                    } else {
                        if (fuelUsage < 0) {
                            "---,--"
                        } else {
                            String.format("%.2f", fuelUsage)
                        }
                    }
                }
            }
        }

        private fun formatFuelAmount(fuel: Float): String {
            return if (fuel > 9999) {
                "+9999"
            } else {
                "+" + String.format("%.0f", fuel)
            }
        }

        private fun formatMileage(m: Float): String {
            return if (m > 999999) {
                "+999999"
            } else {
                String.format("%.1f", m)
            }
        }
    }
