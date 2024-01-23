package com.pepe.mycars.app.ui.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.pepe.mycars.app.ui.view.dialog.RefillDialog
import com.pepe.mycars.databinding.FragmentHistoryBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HistoryFragment : Fragment() {

    private lateinit var binding: FragmentHistoryBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHistoryBinding.inflate(inflater, container, false)

        binding.floatingRefillButton.setOnClickListener {
            RefillDialog().show(requireActivity().supportFragmentManager, "refillDialog")
        }

        return binding.root
    }
}