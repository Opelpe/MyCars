package com.pepe.mycars.app.ui.view.fragment

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
import com.pepe.mycars.app.data.local.MainViewModel
import com.pepe.mycars.app.ui.view.dialog.RefillDialog
import com.pepe.mycars.app.utils.NetworkManager
import com.pepe.mycars.app.utils.displayToast
import com.pepe.mycars.app.utils.state.view.MainViewState
import com.pepe.mycars.app.utils.state.view.UserViewState
import com.pepe.mycars.app.viewmodel.MainViewViewModel
import com.pepe.mycars.databinding.FragmentMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding
    private val mainViewViewModel: MainViewViewModel by activityViewModels()
    var globalMenuItem: MenuItem? = null
    private var isGuest: Boolean = false
    private var isOnline: Boolean = true
    private val refillDialog = RefillDialog()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)

        NetworkManager(requireContext()).observe(viewLifecycleOwner) {
            isOnline = it
            setToolbarIcon()
            if (!it) requireActivity().displayToast("Check your internet connection")
        }

        observeUserViewState()
        observeDataViewState()

        binding.refillButton.setOnClickListener {
            refillDialog.show(requireActivity().supportFragmentManager, "refillDialog")
        }

        initToolbar()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.main_nav_menu, menu)
                globalMenuItem = menu.findItem(R.id.action_synchronize)
                mainViewViewModel.getUserSyncState()
                setToolbarIcon()
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_synchronize -> {
                        if (!isOnline) {
                            requireActivity().displayToast("Check your internet connection")
                        } else {
                            mainViewViewModel.actionSynchronize()
                        }
                        setToolbarIcon()
                        Log.d("LOG_MESSAGE", "Action - 1")
                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    override fun onStart() {
        super.onStart()
        mainViewViewModel.getListOfRefills()
    }

    private fun observeUserViewState() {
        mainViewViewModel.getUserSyncState()
        mainViewViewModel.userMainViewState.observe(viewLifecycleOwner) { viewState ->
            when (viewState) {
                UserViewState.Loading -> {}

                is UserViewState.Error -> {
                    if (viewState.errorMsg.isNotEmpty()) {
                        requireActivity().displayToast(viewState.errorMsg)
                    }
                }

                is UserViewState.Success -> {
                    isGuest = viewState.isAnonymous
                    setToolbarIcon()
                }
            }
        }
    }

    private fun observeDataViewState() {
        mainViewViewModel.getListOfRefills()
        mainViewViewModel.dataMainViewState.observe(viewLifecycleOwner){ viewState ->
            when(viewState){
                MainViewState.Loading->{}
                is MainViewState.Error->{
                    if (viewState.errorMsg.isNotEmpty()) {
                        requireActivity().displayToast(viewState.errorMsg)
                    }
                }
                is MainViewState.Success-> {
                    if (viewState.successMsg.isNotEmpty()) {
                        requireActivity().displayToast(viewState.successMsg)
                    }
                    setMainViewScore(viewState.data)
                }
            }
        }
    }

    private fun setMainViewScore(model: MainViewModel) {

        if (model.avrUsage.isNotEmpty()) {
            binding.averageUsageScoreTitle.text = model.avrUsage
        }
        if (model.avrCosts.isNotEmpty()) {
            binding.travelingCostsScoreTitle.text = model.avrCosts
        }
        if (model.lastCost.isNotEmpty()) {
            binding.lastCostTitle.text = model.lastCost
        }
        if (model.lastMileage.isNotEmpty()) {
            binding.lastMileageTitle.text = model.lastMileage
        }
        if (model.lastUsage.isNotEmpty()) {
            binding.lastUsageTitle.text = model.lastUsage
        }
        if (model.totalMileage.isNotEmpty()) {
            binding.totalMileageTitle.text = model.totalMileage
        }
        if (model.totalCost.isNotEmpty()) {
            binding.totalCostTitle.text = model.totalCost
        }
        if (model.totalAmount.isNotEmpty()) {
            binding.totalAddedFuelTitle.text = model.totalAmount
        }
    }

    private fun initToolbar() {
        val toolbar = binding.mainFragmentToolbar.root
        setToolbarIcon()
        try {
            (activity as AppCompatActivity?)!!.setSupportActionBar(toolbar)
            val bar = (activity as AppCompatActivity?)!!.supportActionBar
            bar!!.title = "MC"
            bar.show()
        } catch (e: Exception) {
            Log.d(
                this.javaClass.name,
                "Setting MainFragment Toolbar EXCEPTION CAPTURED: $e"
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