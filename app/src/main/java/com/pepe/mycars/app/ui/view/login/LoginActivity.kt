package com.pepe.mycars.app.ui.view.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.pepe.mycars.app.MyCarsApp
import com.pepe.mycars.app.ui.view.main.MainViewActivity
import com.pepe.mycars.app.ui.viewmodel.login.LoginViewModel
import com.pepe.mycars.app.ui.viewmodel.splash.SplashViewModel
import com.pepe.mycars.databinding.ActivityLoginBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity: AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val vm: LoginViewModel by viewModels()
        vm.viewState.observe(this){

            if (it.showToastMessage){
                Toast.makeText(this, it.toastMessage, Toast.LENGTH_SHORT).show()

            }

            if (it.loggedIn){
                startMainViewActivity()
            }
        }

        binding.localLoginButton.setOnClickListener { v ->
            vm.onGuestButtonClicked()
        }

    }

    private fun startMainViewActivity() {
        intent = Intent(this, MainViewActivity::class.java)
        startActivity(intent)
    }
}

data class LoginViewModelState(
    val showToastMessage: Boolean = false,
    val loggedIn: Boolean = false,
    val toastMessage: String = "",
)