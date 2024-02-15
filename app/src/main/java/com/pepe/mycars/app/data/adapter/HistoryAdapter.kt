package com.pepe.mycars.app.data.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
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
                    historyHolder.dateTitle.text = refillList[i].refillDate
                    if (data[i].refillDate.isNotEmpty()) {
                        historyHolder.dateTitle.text = refillList[i].refillDate
                    }
                    if (data[i].currMileage.isNotEmpty()) {
                        historyHolder.mileageTitle.text = data[i].currMileage
                    }
                    if (data[i].fuelAmount.isNotEmpty()) {
                        historyHolder.fuelAmountTitle.text = data[i].fuelAmount
                    }
                    if (data[i].fuelCost.isNotEmpty()) {
                        historyHolder.expenseTitle.text = data[i].fuelCost
                    }
                    if (data[i].addedMileage.isNotEmpty()) {
                        historyHolder.addedMileageTitle.text = data[i].addedMileage
                    }
                    if (data[i].fuelUsage.isNotEmpty()) {
                        historyHolder.averageUsageTitle.text = data[i].fuelUsage
                    }
                    if (position == data.size - 1) {
                        historyHolder.dateTitle.text = refillList[i].refillDate
                        historyHolder.mileageTitle.text = refillList[i].currMileage
                        historyHolder.fuelAmountTitle.text = refillList[i].fuelAmount
                        historyHolder.expenseTitle.text = refillList[i].fuelCost
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
       return data.size
    }


internal class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var dateTitle: TextView
    var mileageTitle: TextView
    var addedMileageTitle: TextView
    var expenseTitle: TextView
    var averageUsageTitle: TextView
    var itemidTitle: TextView
    var fuelAmountTitle: TextView
    var deleteItemButton: ImageView

    init {
        dateTitle = itemView.findViewById(R.id.historyDateTitle)
        mileageTitle = itemView.findViewById(R.id.historyMileageTitle)
        addedMileageTitle = itemView.findViewById(R.id.historyAddedMileageTitle)
        expenseTitle = itemView.findViewById(R.id.historyExpenseTitle)
        averageUsageTitle = itemView.findViewById(R.id.historyAvrUsageTitle)
        itemidTitle = itemView.findViewById(R.id.historyItemIdText)
        fuelAmountTitle = itemView.findViewById(R.id.historyLittersText)
        deleteItemButton = itemView.findViewById(R.id.deleteHistoryButton)
    }
}

}

