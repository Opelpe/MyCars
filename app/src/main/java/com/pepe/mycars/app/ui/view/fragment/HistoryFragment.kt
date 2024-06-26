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
import androidx.recyclerview.widget.LinearLayoutManager
import com.pepe.mycars.app.data.adapter.HistoryAdapter
import com.pepe.mycars.app.data.local.HistoryItemUiModel
import com.pepe.mycars.app.ui.view.dialog.RefillDialog
import com.pepe.mycars.app.utils.displayToast
import com.pepe.mycars.app.utils.state.view.HistoryItemViewState
import com.pepe.mycars.app.viewmodel.HistoryViewModel
import com.pepe.mycars.databinding.FragmentHistoryBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HistoryFragment : Fragment() {

    private val historyViewModel: HistoryViewModel by activityViewModels()

    private lateinit var binding: FragmentHistoryBinding

    private var adapter: HistoryAdapter? = null

    private val deleteItemListener: HistoryAdapter.ItemDeleteListener = HistoryAdapter.ItemDeleteListener { itemID -> confirmToCancel(itemID) }

    private val editItemListener: HistoryAdapter.ItemEditListener = HistoryAdapter.ItemEditListener { itemID -> displayEditDialog(itemID) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentHistoryBinding.inflate(inflater, container, false)
        historyViewModel.updateView()
        historyViewModel.observeRefillList(viewLifecycleOwner)
        observeItemSate()
        binding.floatingRefillButton.setOnClickListener {
            val dialog = RefillDialog()
            dialog.show(requireActivity().supportFragmentManager, "refillDialog")
        }

        return binding.root
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

    private fun displayEditDialog(itemId: String) {
        RefillDialog.newInstance(true, itemId)
    }

    private fun confirmToCancel(itemId: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Do you want to remove?")
            .setCancelable(true)
            .setPositiveButton("Yes") { dialog: DialogInterface, _: Int ->
                dialog.dismiss()
                historyViewModel.deleteItem(itemId)
                historyViewModel.updateView()
            }.setNegativeButton("No") { dialog: DialogInterface, _: Int ->
                dialog.dismiss()
//                historyViewModel.updateView()
            }.setOnCancelListener {
//                historyViewModel.updateView()
            }.show()
    }

    private fun setHistoryItems(data: List<HistoryItemUiModel>) {
        if (adapter != null) {
            adapter!!.refreshList(data)
        } else {
            adapter = HistoryAdapter(data, deleteItemListener, editItemListener)
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