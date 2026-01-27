package com.pepe.mycars.app.data.mapper

import com.pepe.mycars.app.data.local.MainScoreModel
import com.pepe.mycars.domain.model.FuelDataInfo
import javax.inject.Inject

class MainViewModelMapper
    @Inject
    constructor() {
        fun mapToMainViewModel(model: List<FuelDataInfo>): MainScoreModel {
            val fAvrUsage = countAvrUsage(model)
            val fAvrPrice = countTravelingCost(model)
            val fCurrMileage = getCurrMileage(model)
            val fLastUsage = getLastUsage(model)
            val fLastCost = getLastCost(model)
            val fTotalAddedMileage = countTotalMileage(model)
            val fTotalCost = getTotalCost(model)
            val fTotalFuelAmount = getTotalFuelAmount(model)

            return MainScoreModel(
                avrUsage = fAvrUsage,
                avrCosts = fAvrPrice,
                lastCost = fLastCost,
                lastUsage = fLastUsage,
                lastMileage = fCurrMileage,
                totalMileage = fTotalAddedMileage,
                totalCost = fTotalCost,
                totalAmount = fTotalFuelAmount,
            )
        }

        private fun getTotalFuelAmount(model: List<FuelDataInfo>): String {
            var totalFuel = 0f
            for (i in model.indices) {
                totalFuel += model[i].fuelAmount!!
            }
            return formatFuelAmount(totalFuel)
        }

        private fun getTotalCost(model: List<FuelDataInfo>): String {
            var totalPrice = 0f
            for (i in model.indices) {
                totalPrice += model[i].fuelPrice!! * model[i].fuelAmount!!
            }
            return formatTotalCost(totalPrice)
        }

        private fun countTotalMileage(model: List<FuelDataInfo>): String {
            return if (model.isNotEmpty() && model.size > 1) {
                val currMileage = model[0].currMileage
                val firstMileage = model.last().currMileage
                val addedMileage = currMileage!! - firstMileage!!
                formatAddedMileage(addedMileage)
            } else {
                "---"
            }
        }

        private fun getLastUsage(model: List<FuelDataInfo>): String {
            return if (model.size > 1) {
                val currMileage = model[0].currMileage
                val lastMileage = model[1].currMileage
                val addedMileage = currMileage!! - lastMileage!!
                if (model[0].fullTank) {
                    return formatFuelUsage((model[0].fuelAmount?.times(100))?.div(addedMileage)!!)
                } else {
                    return "---"
                }
            } else {
                "---"
            }
        }

        private fun getCurrMileage(model: List<FuelDataInfo>): String {
            return if (model.isEmpty()) {
                "---"
            } else {
                formatMileage(model[0].currMileage)
            }
        }

        private fun getLastCost(model: List<FuelDataInfo>): String {
            return if (model.isNotEmpty()) {
                val price = model[0].fuelPrice
                val amount = model[0].fuelAmount
                formatFuelCost(price, amount)
            } else {
                "---"
            }
        }

        private fun formatFuelCost(
            fp: Float?,
            fa: Float?,
        ): String {
            return if (fp != null && fa != null) {
                val fuelCost = fp * fa
                if (fuelCost > 9999) {
                    "+9999"
                } else {
                    if (fuelCost > 999) {
                        String.format("%.0f", fuelCost)
                    } else {
                        String.format("%.2f", fuelCost)
                    }
                }
            } else {
                "---"
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
                if (fuel <= 0) {
                    "---"
                } else {
                    "+" + String.format("%.0f", fuel)
                }
            }
        }

        private fun formatMileage(m: Float?): String {
            return if (m != null) {
                if (m > 999999) {
                    "+999999"
                } else {
                    String.format("%.1f", m)
                }
            } else {
                "---"
            }
        }

        private fun countAvrUsage(model: List<FuelDataInfo>): String {
            var usage = 0f
            var itemsCount = 0
            for (i in model.indices) {
                if (i + 1 < model.size) {
                    val currentMileage = model[i].currMileage ?: 0f
                    val lastMileage = model[i + 1].currMileage ?: 0f
                    val addedMileage = currentMileage - lastMileage
                    if (model[i].fullTank) {
                        usage += (model[i].fuelAmount?.times(100))?.div(addedMileage)!!
                        itemsCount++
                    }
                }
            }
            return if (model.size < 2) {
                formatAvrUsage(usage)
            } else {
                formatAvrUsage(usage / itemsCount)
            }
        }

        private fun countTravelingCost(model: List<FuelDataInfo>): String {
            if (model.isNotEmpty() && model.size > 1) {
                var price = 0f
                var currentMileage = 0f
                var lastMileage = 0f

                for (i in model.indices) {
                    if (i == 0) {
                        currentMileage = model[i].currMileage!!
                    }
                    if (i == model.size - 1) {
                        lastMileage = model[i].currMileage!!
                    }

                    if (i + 1 < model.size) {
                        price += (model[i].fuelPrice!! * model[i].fuelAmount!!)
                    }
                }
                val addedMileage = currentMileage - lastMileage
                val score = price / addedMileage
                val finalScore = score * 100
                return formatTravelingCost(finalScore)
            } else {
                return "---,--"
            }
        }

        private fun formatTravelingCost(avrPrice: Float): String {
            return if (avrPrice > 999.99) {
                "+999"
            } else {
                if (avrPrice > 9.99) {
                    String.format("%.1f", avrPrice)
                } else {
                    if (avrPrice <= 0) {
                        "---,--"
                    } else {
                        String.format("%.2f", avrPrice)
                    }
                }
            }
        }

        private fun formatTotalCost(totalCost: Float): String {
            return if (totalCost > 99999.99) {
                "+99999"
            } else {
                if (totalCost > 9.99) {
                    String.format("%.1f", totalCost)
                } else {
                    if (totalCost <= 0) {
                        "---,--"
                    } else {
                        String.format("%.2f", totalCost)
                    }
                }
            }
        }

        private fun formatAvrUsage(usage: Float): String {
            return if (usage > 9.99) {
                String.format("%.1f", usage)
            } else {
                if (usage <= 0) {
                    "---,--"
                } else {
                    String.format("%.2f", usage)
                }
            }
        }
    }
