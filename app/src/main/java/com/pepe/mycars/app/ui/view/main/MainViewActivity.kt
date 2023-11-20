package com.pepe.mycars.app.ui.view.main

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.pepe.mycars.app.ui.view.login.LoginActivity
import com.pepe.mycars.app.ui.viewmodel.main.MainViewModel
import com.pepe.mycars.app.ui.viewmodel.splash.SplashViewModel
import com.pepe.mycars.databinding.ActivityMainViewBinding
import com.pepe.mycars.databinding.ActivitySplashBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainViewActivity : AppCompatActivity() {

    private var binding: ActivityMainViewBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainViewBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        val vm: MainViewModel by viewModels()

        vm.viewState.observe(this){

            if (it.loggedIn){
                Toast.makeText(this, "zalogfowano", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this,"niezalogowano", Toast.LENGTH_SHORT).show()
                startLoginActivity()
            }
        }
    }

    private fun startLoginActivity() {
        intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

}

data class MainViewModelState(
    val showToastMessage: Boolean = false,
    val loggedIn: Boolean = true,
    val toastMessage: String = "",
)