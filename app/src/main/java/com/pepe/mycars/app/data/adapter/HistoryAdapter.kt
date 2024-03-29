package com.pepe.mycars.app.data.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pepe.mycars.R
import com.pepe.mycars.app.data.local.HistoryItemUiModel

class HistoryAdapter(data: List<HistoryItemUiModel>, itemDeleteListener: ItemDeleteListener, itemEditListener: ItemEditListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var refillList = data

    private val deleteListener = itemDeleteListener

    private val editListener = itemEditListener

    fun interface ItemDeleteListener {
        fun onDeleteClick(itemId: String)
    }

    fun interface ItemEditListener {
        fun onLongClick(itemId: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val historyView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_history, parent, false)

        return HistoryViewHolder(historyView)

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val historyHolder = holder as HistoryViewHolder

        if (refillList.isNotEmpty()) {

            historyHolder.deleteItemButton.setOnClickListener {
                deleteListener.onDeleteClick(refillList[position].itemId)
            }

            historyHolder.itemView.setOnLongClickListener {
                editListener.onLongClick(refillList[position].itemId)
                true
            }

            val refillList = refillList
            for (i in refillList.indices) {
                if (position == i) {
                    historyHolder.dateTitle.text = refillList[i].refillDate
                    historyHolder.dateTitle.text = refillList[i].refillDate
                    historyHolder.mileageTitle.text = this.refillList[i].currMileage
                    historyHolder.fuelAmountTitle.text = this.refillList[i].fuelAmount
                    historyHolder.expenseTitle.text = this.refillList[i].fuelCost
                    historyHolder.addedMileageTitle.text = this.refillList[i].addedMileage
                    historyHolder.averageUsageTitle.text = this.refillList[i].fuelUsage

                    if (position == this.refillList.size - 1) {
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
        return refillList.size
    }

    fun refreshList(data: List<HistoryItemUiModel>) {
        refillList = data
        this.notifyDataSetChanged()
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

