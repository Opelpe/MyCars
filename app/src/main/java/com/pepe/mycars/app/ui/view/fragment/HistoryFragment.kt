package com.pepe.mycars.app.ui.view.fragment

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pepe.mycars.app.data.adapter.HistoryAdapter
import com.pepe.mycars.app.data.local.HistoryItemUiModel
import com.pepe.mycars.app.ui.view.dialog.DialogMode
import com.pepe.mycars.app.ui.view.dialog.RefillDialog
import com.pepe.mycars.app.utils.displayToast
import com.pepe.mycars.app.utils.state.view.HistoryItemViewState
import com.pepe.mycars.app.viewmodel.HistoryViewModel
import com.pepe.mycars.databinding.FragmentHistoryBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HistoryFragment : Fragment() {

    private lateinit var binding: FragmentHistoryBinding

    private val historyViewModel: HistoryViewModel by activityViewModels()

    private lateinit var itemTouchHelper: ItemTouchHelper

    private var adapter: HistoryAdapter? = null

    private var historyList: List<HistoryItemUiModel> = listOf()

    private val deleteItemListener: HistoryAdapter.ItemDeleteListener =
        HistoryAdapter.ItemDeleteListener { itemID -> itemDeletionConfirmationDialog(itemID) }

    private val editItemListener: HistoryAdapter.ItemEditListener =
        HistoryAdapter.ItemEditListener { itemID -> displayRefillDialog(itemID, DialogMode.EDIT) }

    private val detailsItemListener: HistoryAdapter.ItemDetailsListener =
        HistoryAdapter.ItemDetailsListener { itemID -> displayRefillDialog(itemID, DialogMode.DETAILS) }

    private val simpleCallback = object :
        ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean = false


        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.adapterPosition

            when (direction) {
                ItemTouchHelper.LEFT -> {
                    val itemId = historyList[position].itemId
                    displayRefillDialog(itemId, DialogMode.EDIT)
                    adapter?.notifyDataSetChanged()
                }

                ItemTouchHelper.RIGHT -> {
                    val itemId = historyList[position].itemId
                    itemDeletionConfirmationDialog(itemId)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentHistoryBinding.inflate(inflater, container, false)
        historyViewModel.updateView()
        historyViewModel.observeRefillList(viewLifecycleOwner)
        observeItemSate()
        binding.floatingRefillButton.setOnClickListener { displayRefillDialog(null, DialogMode.ADD) }

        itemTouchHelper = ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(binding.historyRecyclerView)

        return binding.root
    }

    private fun displayRefillDialog(itemID: String?, dialogMode: DialogMode) {
        if (itemID != null && dialogMode != DialogMode.ADD) {
            RefillDialog.newInstance(dialogMode, itemID).show(requireActivity().supportFragmentManager, "refillDialog")
        } else {
            if (dialogMode == DialogMode.ADD) {
                RefillDialog().show(requireActivity().supportFragmentManager, "refillDialog")
            }
        }
    }

    private fun observeItemSate() {

        historyViewModel.historyItemViewState.observe(viewLifecycleOwner) {
            when (it) {
                HistoryItemViewState.Loading -> {
                    setProgressVisibility(true)
                }

                is HistoryItemViewState.Error -> {
                    if (it.errorMsg.isNotBlank()) {
                        requireActivity().displayToast(it.errorMsg)
                    }
                    setProgressVisibility(false)
                }

                is HistoryItemViewState.Success -> {

                    if (it.successMsg.isNotBlank()) {
                        requireActivity().displayToast(it.successMsg)
                    }

                    setHistoryItems(it.data)
                    setProgressVisibility(false)
                }
            }
        }
    }

    private fun itemDeletionConfirmationDialog(itemId: String) {
        AlertDialog.Builder(requireContext()).setTitle("Do you want to remove?").setCancelable(true)
            .setPositiveButton("Yes") { dialog: DialogInterface, _: Int ->
                dialog.dismiss()
                historyViewModel.deleteItem(itemId)
            }.setNegativeButton("No") { dialog: DialogInterface, _: Int ->
                dialog.dismiss()
                adapter?.notifyDataSetChanged()
            }
            .setOnCancelListener {
                adapter?.notifyDataSetChanged()
            }.show()
    }

    private fun setHistoryItems(data: List<HistoryItemUiModel>) {
        historyList = data
        if (adapter != null) {
            adapter!!.refreshList(data)
        } else {
            adapter = HistoryAdapter(data, deleteItemListener, editItemListener, detailsItemListener)
            binding.historyRecyclerView.layoutManager = LinearLayoutManager(context)
            binding.historyRecyclerView.adapter = adapter
        }

    }

    private fun setProgressVisibility(loading: Boolean) {
        if (loading) {
            requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            binding.progressView.visibility = View.VISIBLE
        } else {
            binding.progressView.visibility = View.GONE
            requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }
    }

}