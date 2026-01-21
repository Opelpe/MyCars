package com.pepe.mycars.app.ui.view.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import com.pepe.mycars.R
import com.pepe.mycars.app.data.local.MainScoreModel
import com.pepe.mycars.app.ui.view.dialog.RefillDialog
import com.pepe.mycars.app.utils.NetworkManager
import com.pepe.mycars.app.utils.SharedPrefConstants
import com.pepe.mycars.app.utils.displayToast
import com.pepe.mycars.app.utils.state.view.MainViewState
import com.pepe.mycars.app.viewmodel.MainViewModel
import com.pepe.mycars.databinding.FragmentMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : Fragment() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var binding: FragmentMainBinding
    private val mainViewModel: MainViewModel by activityViewModels()
    var globalMenuItem: MenuItem? = null
    private var isGuest: Boolean = false
    private var isOnline: Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        sharedPreferences = requireContext().getSharedPreferences(SharedPrefConstants.LOCAL_SHARED_PREF, Context.MODE_PRIVATE)

        NetworkManager(requireContext()).observe(viewLifecycleOwner) {
            isOnline = it
            setToolbarIcon()
            if (!it) requireActivity().displayToast("Check your internet connection")
        }

        observeUserViewState()
        observeDataViewState()

        binding.refillButton.setOnClickListener {
            RefillDialog().show(requireActivity().supportFragmentManager, "refillDialog")
        }

        initToolbar()
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().addMenuProvider(
            object : MenuProvider {
                override fun onCreateMenu(
                    menu: Menu,
                    menuInflater: MenuInflater,
                ) {
                    menuInflater.inflate(R.menu.main_nav_menu, menu)
                    globalMenuItem = menu.findItem(R.id.action_synchronize)
                    setToolbarIcon()
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    return when (menuItem.itemId) {
                        R.id.action_synchronize -> {
                            if (!isOnline) {
                                requireActivity().displayToast("Check your internet connection")
                            } else {
                                mainViewModel.actionSynchronize()
                            }
                            setToolbarIcon()
                            Log.d("LOG_MESSAGE", "Action - 1")
                            true
                        }

                        else -> false
                    }
                }
            },
            viewLifecycleOwner,
            Lifecycle.State.RESUMED,
        )
    }

    private fun observeUserViewState() {
        isGuest = mainViewModel.isUserAnonymous()
        setToolbarIcon()
    }

    private fun observeDataViewState() {
        mainViewModel.getListOfRefills()
        mainViewModel.observeRefillList(viewLifecycleOwner)
        mainViewModel.dataMainViewState.observe(viewLifecycleOwner) { viewState ->
            when (viewState) {
                MainViewState.Loading -> {}
                is MainViewState.Error -> {
                    if (viewState.errorMsg.isNotEmpty()) {
                        requireActivity().displayToast(viewState.errorMsg)
                    }
                }
                is MainViewState.Success -> {
                    if (viewState.successMsg.isNotEmpty()) {
                        requireActivity().displayToast(viewState.successMsg)
                    }
                    setMainViewScore(viewState.data)
                }
            }
        }
    }

    private fun setMainViewScore(model: MainScoreModel) {
        binding.averageUsageScoreTitle.text = model.avrUsage
        binding.travelingCostsScoreTitle.text = model.avrCosts
        binding.lastCostTitle.text = model.lastCost
        binding.lastMileageTitle.text = model.lastMileage
        binding.lastUsageTitle.text = model.lastUsage
        binding.totalMileageTitle.text = model.totalMileage
        binding.totalCostTitle.text = model.totalCost
        binding.totalAddedFuelTitle.text = model.totalAmount
    }

    private fun initToolbar() {
        val toolbar = binding.mainFragmentToolbar.root
        setToolbarIcon()
        try {
            (activity as AppCompatActivity?)!!.setSupportActionBar(toolbar)
            val bar = (activity as AppCompatActivity?)!!.supportActionBar
            bar!!.title = "MyCars"
            bar.show()
        } catch (e: Exception) {
            Log.d(
                this.javaClass.name,
                "Setting MainFragment Toolbar EXCEPTION CAPTURED: $e",
            )
        }
    }

    private fun setToolbarIcon() {
        if (isOnline) {
            if (isGuest) {
                if (globalMenuItem != null) globalMenuItem!!.setIcon(R.drawable.ic_cloud_off)
            } else {
                if (globalMenuItem != null) globalMenuItem!!.setIcon(R.drawable.ic_cloud_on)
            }
        } else {
            if (globalMenuItem != null) globalMenuItem!!.setIcon(R.drawable.ic_cloud_off)
        }
    }
}
