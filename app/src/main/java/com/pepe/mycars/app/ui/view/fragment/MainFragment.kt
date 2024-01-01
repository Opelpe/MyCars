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
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import com.pepe.mycars.R
import com.pepe.mycars.app.viewmodel.LoggedInViewModel
import com.pepe.mycars.databinding.FragmentMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding
    private val loggedInViewModel: LoggedInViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        setMainFragmentToolbar()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.main_nav_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_synchronize -> {
                        loggedInViewModel.actionSynchronize(1)
                        Log.d("LOG_MESSAGE", "Action - 1")
                        true
                    }

                    R.id.action_change_google_account -> {
                        loggedInViewModel.actionSynchronize(2)
                        Log.d("LOG_MESSAGE", "Action - 2")
                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }
    private fun setMainFragmentToolbar() {
        val toolbar = binding.mainFragmentToolbar.root
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


}