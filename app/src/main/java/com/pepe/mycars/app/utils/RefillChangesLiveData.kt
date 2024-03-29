package com.pepe.mycars.app.utils

import android.util.Log
import androidx.lifecycle.LiveData
import com.google.firebase.firestore.CollectionReference
import com.pepe.mycars.app.data.model.HistoryItemModel

class RefillChangesLiveData(collection: CollectionReference) :
    LiveData<List<HistoryItemModel>>() {


    private val listener = collection.addSnapshotListener { v, e ->
        if (e != null) {
            Log.w(this.javaClass.name, "Listen failed.", e)
            return@addSnapshotListener
        }

        val refillList = mutableListOf<HistoryItemModel>()
        for (doc in v!!) {
            val item = doc.toObject(HistoryItemModel::class.java)
            refillList.add(item)
        }
        value = refillList.sortedByDescending { it.currMileage }
        Log.d(this.javaClass.name, "ItemsListener -> current items: ${refillList.sortedByDescending { it.currMileage }.size}")
    }

    override fun onInactive() {
        super.onInactive()
        listener.remove()
    }
}