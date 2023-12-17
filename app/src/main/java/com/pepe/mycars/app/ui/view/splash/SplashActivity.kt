package com.pepe.mycars.app.ui.view.splash

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.pepe.mycars.app.ui.view.login.LoginActivity
import com.pepe.mycars.app.ui.view.main.MainViewActivity
import com.pepe.mycars.app.utils.displayToast
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


        loggedInViewModel.userStateModel.observe(this) {
            val user = it.data
            if (user != null) {
                if (user.autoLogin) {
                    startMainViewActivity()
                } else {
                    startLoginActivity()
                }
            }

            if (it.error.isNotEmpty()){
                displayToast(it.error)
                startLoginActivity()
            }
        }

        loggedInViewModel.getUserData()
    }

    private fun startLoginActivity() {
        intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun startMainViewActivity() {
        intent = Intent(this, MainViewActivity::class.java)
        startActivity(intent)
        finish()
    }
}