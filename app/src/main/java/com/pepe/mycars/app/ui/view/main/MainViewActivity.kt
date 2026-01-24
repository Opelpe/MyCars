package com.pepe.mycars.app.ui.view.main

import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.pepe.mycars.R
import com.pepe.mycars.app.ui.view.login.LoginActivity
import com.pepe.mycars.app.utils.SharedPrefConstants
import com.pepe.mycars.app.utils.displayToast
import com.pepe.mycars.app.utils.state.view.UserViewState
import com.pepe.mycars.app.viewmodel.LoggedInViewModel
import com.pepe.mycars.databinding.ActivityMainViewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainViewActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var binding: ActivityMainViewBinding
    private val loggedInViewModel: LoggedInViewModel by viewModels()

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission(),
        ) { isGranted: Boolean ->
            if (isGranted) {
                Firebase.messaging.subscribeToTopic("all")
                    .addOnCompleteListener { task ->
                        val msg = if (task.isSuccessful) "Subscribed" else "Subscribe failed"
                        Log.d("FCM", msg)
                    }
            } else {
                displayToast(getString(R.string.notification_disabled_title))
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainViewBinding.inflate(layoutInflater)
        sharedPreferences =
            applicationContext.getSharedPreferences(
                SharedPrefConstants.LOCAL_SHARED_PREF,
                Context.MODE_PRIVATE,
            )
        setContentView(binding.root)
        setNavigation()
        askNotificationPermission()
        observeUserViewSate()
    }

    private fun observeUserViewSate() {
        loggedInViewModel.userViewState.observe(this) {
            when (it) {
                UserViewState.Loading -> setProgressVisibility(true)
                is UserViewState.Error -> {
                    setProgressVisibility(false)
                    if (it.errorMsg.isNotEmpty()) {
                        displayToast(it.errorMsg)
                    }
                }

                is UserViewState.Success -> {
                    setProgressVisibility(false)
                    if (it.successMsg.isNotEmpty()) {
                        displayToast(it.successMsg)
                    }
                    if (!it.isLoggedIn) {
                        startLoginActivity()
                    }
                }
            }
        }
    }

    private fun setNavigation() {
        val navView = binding.navView
        val navigationHost =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        val navController = navigationHost.navController
        NavigationUI.setupWithNavController(navView, navController)
    }

    private fun startLoginActivity() {
        setProgressVisibility(true)
        startActivity(Intent(this@MainViewActivity, LoginActivity::class.java))
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(POST_NOTIFICATIONS)
            }
        }
    }

    private fun setProgressVisibility(loading: Boolean) {
        if (loading) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            )
            binding.progressView.visibility = View.VISIBLE
        } else {
            binding.progressView.visibility = View.GONE
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }
    }
}
