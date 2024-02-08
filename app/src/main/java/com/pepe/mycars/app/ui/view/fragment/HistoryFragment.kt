package com.pepe.mycars.app.ui.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.pepe.mycars.app.data.adapter.HistoryAdapter
import com.pepe.mycars.app.data.local.HistoryItemUiModel
import com.pepe.mycars.app.data.model.HistoryItemModel
import com.pepe.mycars.app.ui.view.dialog.RefillDialog
import com.pepe.mycars.app.utils.displayToast
import com.pepe.mycars.app.utils.state.view.HistoryItemViewState
import com.pepe.mycars.app.viewmodel.ItemHistoryViewModel
import com.pepe.mycars.databinding.FragmentHistoryBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HistoryFragment : Fragment() {

    private val itemHistoryViewModel: ItemHistoryViewModel by activityViewModels()

    private lateinit var binding: FragmentHistoryBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHistoryBinding.inflate(inflater, container, false)
        itemHistoryViewModel.getListOfRefills()
        observeItemSate()
        binding.floatingRefillButton.setOnClickListener {
            val dialog = RefillDialog()
            dialog.show(requireActivity().supportFragmentManager, "refillDialog")
        }

        return binding.root
    }

    private fun setHistoryItems(data: List<HistoryItemUiModel>) {
        binding.historyRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.historyRecyclerView.adapter = HistoryAdapter(data)
    }

    override fun onStart() {
        super.onStart()
        itemHistoryViewModel.getListOfRefills()
    }

    private fun observeItemSate() {

        itemHistoryViewModel.historyItemViewState.observe(viewLifecycleOwner) {
            when (it) {
                HistoryItemViewState.Loading -> {
//                    setProgressVisibility(true)
                }

                is HistoryItemViewState.Error -> {
                    if (it.errorMsg.isNotBlank()) {
                        requireActivity().displayToast(it.errorMsg)
                    }
//                    setProgressVisibility(false)
                }

                is HistoryItemViewState.Success -> {

//                    if (it.successMsg.isNotBlank()) {
//                        requireActivity().displayToast(it.successMsg)
//                    }
//
//                    if (it.successMsg == "Saved!") {
//                        dismissDialog("refillDialog")
//                        itemHistoryViewModel.getListOfRefills()
//                    }

                    setHistoryItems(it.data)


//                    setProgressVisibility(false)
                }
            }
        }
    }

    private fun dismissDialog(tag: String){
            requireActivity().supportFragmentManager.findFragmentByTag(tag)?.let {
                if(it is DialogFragment) it.dismiss() }
    }

}