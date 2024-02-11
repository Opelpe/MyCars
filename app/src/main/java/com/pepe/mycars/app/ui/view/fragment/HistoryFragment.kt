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
import com.pepe.mycars.app.ui.view.dialog.RefillDialog
import com.pepe.mycars.app.utils.displayToast
import com.pepe.mycars.app.utils.state.view.HistoryItemViewState
import com.pepe.mycars.app.viewmodel.HistoryViewViewModel
import com.pepe.mycars.databinding.FragmentHistoryBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HistoryFragment : Fragment() {

    private val historyViewViewModel: HistoryViewViewModel by activityViewModels()

    private lateinit var binding: FragmentHistoryBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHistoryBinding.inflate(inflater, container, false)
        historyViewViewModel.getListOfRefills()
        observeItemSate()
        binding.floatingRefillButton.setOnClickListener {
            val dialog = RefillDialog()
            dialog.show(requireActivity().supportFragmentManager, "refillDialog")
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        historyViewViewModel.getListOfRefills()
    }

    private fun observeItemSate() {

        historyViewViewModel.historyItemViewState.observe(viewLifecycleOwner) {
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

                    if (it.successMsg.isNotBlank()) {
                        requireActivity().displayToast(it.successMsg)
                    }
                    if (it.successMsg == "Saved!") {
                        historyViewViewModel.getListOfRefills()
                    }
                    setHistoryItems(it.data)
//                  setProgressVisibility(false)
                }
            }
        }
    }

    private fun setHistoryItems(data: List<HistoryItemUiModel>) {
        binding.historyRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.historyRecyclerView.adapter = HistoryAdapter(data)
    }

}