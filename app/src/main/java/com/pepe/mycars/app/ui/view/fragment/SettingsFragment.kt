package com.pepe.mycars.app.ui.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.pepe.mycars.R
import com.pepe.mycars.app.viewmodel.LoggedInViewModel
import com.pepe.mycars.databinding.FragmentSettingsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding
    private val loggedInViewModel: LoggedInViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.logoutButton.setOnClickListener {
            loggedInViewModel.logOut()
        }

        binding.languagesContainer.setOnClickListener {
            if (binding.languagesSetContainer.isVisible){
                val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_arrow_forward)
                binding.languagesSetContainer.visibility = View.GONE
                binding.languagesTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(drawable,null,null,null)
            }else{
                val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_double_arrow_down)
                binding.languagesSetContainer.visibility = View.VISIBLE
                binding.languagesTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(drawable,null,null,null)
            }
        }
    }
}