package com.pepe.mycars.app.ui.view.splash

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.pepe.mycars.app.ui.view.login.LoginActivity
import com.pepe.mycars.app.ui.view.main.MainViewActivity
import com.pepe.mycars.app.utils.displayToast
import com.pepe.mycars.app.utils.networkState.UserViewState
import com.pepe.mycars.app.viewmodel.LoggedInViewModel
import com.pepe.mycars.databinding.ActivitySplashBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val loggedInViewModel: LoggedInViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        observeUserViewSate()
    }

    private fun observeUserViewSate() {
        loggedInViewModel.appStart()
        loggedInViewModel.userViewState.observe(this) {
            when (it) {
                is UserViewState.Error -> {
                    if (it.errorMsg.isNotEmpty()) {
                        displayToast(it.errorMsg)
                        displayActivity(ActivityId.LOGIN)
                    }
                }

                is UserViewState.Success -> {
                    if (it.successMsg.isNotEmpty()) displayToast(it.successMsg)
                    if (it.isLoggedIn) displayActivity(ActivityId.MAIN) else displayActivity(ActivityId.LOGIN)
                }

                else -> {}
            }
        }
    }

    private fun displayActivity(activity: ActivityId) {
        when(activity){
            ActivityId.LOGIN -> startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
            ActivityId.MAIN -> startActivity(Intent(this@SplashActivity, MainViewActivity::class.java))
        }
        finish()
    }
}

enum class ActivityId{
    LOGIN,
    MAIN
}