package com.pepe.mycars.app.data.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pepe.mycars.R
import com.pepe.mycars.app.data.local.HistoryItemUiModel

class HistoryAdapter(val data: List<HistoryItemUiModel>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val historyView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_history, parent, false)

        return HistoryViewHolder(historyView)

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val historyHolder = holder as HistoryViewHolder

        if (data.isNotEmpty()) {
            historyHolder.deleteItemButton.setOnClickListener { view: View? ->
//                    itemModelList.get(position).itemID
            }
            historyHolder.itemView.setOnClickListener { view: View? ->
//                    itemModelList.get(position).itemID
            }

            val refillList = data

            for (i in refillList.indices) {
                if (position == i) {
                    historyHolder.hDateText.setText(refillList[i].refillDate)
                    historyHolder.hMileageText.setText(refillList[i].currMileage)
                    historyHolder.hLittersText.setText(refillList[i].fuelAmount)
                    historyHolder.hExpensesText.setText(refillList[i].fuelCost)
                    historyHolder.hAddedMileageText.setText(data[i].addedMileage)
                    if (!data[i].fuelUsage.isNullOrEmpty()) {
                        historyHolder.hAverageUsageText.setText(data[i].fuelUsage)
                    }
                    //                        }
                    if (position == data.size - 1) {
                        historyHolder.hDateText.setText(refillList[i].refillDate)
                        historyHolder.hMileageText.setText(refillList[i].currMileage)
                        historyHolder.hLittersText.setText(refillList[i].fuelAmount)
                        historyHolder.hExpensesText.setText(refillList[i].fuelCost)
                    }
                }
            }
        }

    }

    override fun getItemCount(): Int {
       return data.size
    }


internal class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var hDateText: TextView
    var hMileageText: TextView
    var hAddedMileageText: TextView
    var hExpensesText: TextView
    var hAverageUsageText: TextView
    var hItemIDText: TextView
    var hLittersText: TextView
    var deleteItemButton: ImageButton

    init {
        hDateText = itemView.findViewById(R.id.historyDateText)
        hMileageText = itemView.findViewById(R.id.historyMileageText)
        hAddedMileageText = itemView.findViewById(R.id.historyAddedMileageText)
        hExpensesText = itemView.findViewById(R.id.historyExpenseText)
        hAverageUsageText = itemView.findViewById(R.id.historyAverageUsageText)
        hItemIDText = itemView.findViewById(R.id.historyItemIdText)
        hLittersText = itemView.findViewById(R.id.historyLittersText)
        deleteItemButton = itemView.findViewById(R.id.deleteHistoryButton)
    }
}

}

