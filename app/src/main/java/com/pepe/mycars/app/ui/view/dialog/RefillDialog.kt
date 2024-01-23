package com.pepe.mycars.app.ui.view.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.pepe.mycars.databinding.DialogRefillBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RefillDialog : DialogFragment() {

    private lateinit var binding: DialogRefillBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DialogRefillBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()

        dialog ?: return
        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val height = ViewGroup.LayoutParams.MATCH_PARENT
        dialog?.window?.setLayout(width, height)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.saveRefillButton.setOnClickListener { }
        binding.cancelButton.setOnClickListener { dismiss() }
    }

}