package com.pepe.mycars.app.ui.view.main

import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
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
import com.pepe.mycars.app.utils.displayToast
import com.pepe.mycars.app.utils.logMessage
import com.pepe.mycars.app.viewmodel.LoggedInViewModel
import com.pepe.mycars.databinding.ActivityMainViewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainViewBinding
    private val loggedInViewModel: LoggedInViewModel by viewModels()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            Firebase.messaging.subscribeToTopic("all")
                .addOnCompleteListener { task ->
                    var msg = "Subscribed"
                    if (!task.isSuccessful) {
                        msg = "Subscribe failed"
                    }
                    Log.d("FCM", msg)
                }
        } else {
            displayToast(getString(R.string.notification_disabled_title))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setNavigation()
//        askNotificationPermission()

        loggedInViewModel.getUserData()
        loggedInViewModel.userStateModel.observe(this) {
            if (it!!.isLoading) {
                logMessage("Loading...")
            }

            if (it.error.isNotEmpty()) {
                logMessage(it.error)
                displayToast(it.error)
            }

            if (it.data != null) {
                logMessage("user id: " + it.data.id)
                val provider = it.data.providerType
                var msg = "Logged as: "
                msg += if (it.data.email.isNotEmpty()){
                    it.data.email + " \n $provider"
                }else{
                    it.data.name
                }
                displayToast(msg)
            }

            if(!it.isLoggedIn){
                logMessage("user loggingOut")
                startLoginActivity()
            }
        }
    }

    private fun setNavigation() {
        val navView = binding.navView
        val navigationHost = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        val navController = navigationHost.navController
        NavigationUI.setupWithNavController(navView, navController)
    }

    private fun startLoginActivity() {
        intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
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

}