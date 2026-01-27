package com.pepe.mycars.app.ui.view.splash

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.pepe.mycars.app.ui.view.login.LoginActivity
import com.pepe.mycars.app.ui.view.main.MainViewActivity
import com.pepe.mycars.app.utils.logMessage
import com.pepe.mycars.app.utils.state.view.UserViewState
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
        loggedInViewModel.userViewState.observe(this) {
            when (it) {
                UserViewState.Loading -> {}
                is UserViewState.Error -> {
                    if (it.errorMsg.isNotEmpty()) {
                        logMessage(it.errorMsg)
                        displayActivity(ActivityId.LOGIN)
                    }
                }

                is UserViewState.Success -> {
                    when {
                        it.isLoggedIn -> displayActivity(ActivityId.MAIN)
                        else -> displayActivity(ActivityId.LOGIN)
                    }
                }
            }
        }
    }

    private fun displayActivity(activity: ActivityId) {
        when (activity) {
            ActivityId.LOGIN -> startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
            ActivityId.MAIN -> startActivity(Intent(this@SplashActivity, MainViewActivity::class.java))
        }
        finish()
    }
}

enum class ActivityId {
    LOGIN,
    MAIN,
}
