package com.pepe.mycars.app.ui.view.splash

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.pepe.mycars.app.ui.view.login.LoginActivity
import com.pepe.mycars.app.ui.view.main.MainViewActivity
import com.pepe.mycars.app.ui.viewmodel.splash.SplashViewModel
import com.pepe.mycars.databinding.ActivitySplashBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private var binding: ActivitySplashBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        val vm: SplashViewModel by viewModels()

        //click -> vm.onClick()

        vm.viewState.observe(this) {

            if (it.showToastMessage) {
                Toast.makeText(this, it.toastMessage, Toast.LENGTH_SHORT).show()
            }

            if (it.loggedIn){
                startMainViewActivity()
            }else{
                startLoginActivity()
            }
        }

    }

    private fun startLoginActivity() {
        intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun startMainViewActivity() {
        intent = Intent(this, MainViewActivity::class.java)
        startActivity(intent)
    }
}

data class SplashViewModelState(
    val showToastMessage: Boolean = false,
    val loggedIn: Boolean = false,
    val toastMessage: String = "",
)