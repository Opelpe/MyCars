package com.pepe.mycars.app.ui.view.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.pepe.mycars.app.ui.view.login.dialog.CreateAccountDialog
import com.pepe.mycars.app.ui.view.login.dialog.LoginDialog
import com.pepe.mycars.app.ui.view.main.MainViewActivity
import com.pepe.mycars.app.utils.displayToast
import com.pepe.mycars.app.utils.state.view.LoginViewState
import com.pepe.mycars.app.viewmodel.AuthViewModel
import com.pepe.mycars.databinding.ActivityLoginBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bindButtons()
        observeAuthState()
    }

    private fun observeAuthState() {
        lifecycleScope.launch {
            authViewModel.loginViewState.collect { state ->
                when (state) {
                    is LoginViewState.Idle -> setProgressVisibility(false)
                    is LoginViewState.Loading -> setProgressVisibility(true)
                    is LoginViewState.Error -> {
                        setProgressVisibility(false)
                        displayToast(state.message)
                    }
                    is LoginViewState.Success -> {
                        setProgressVisibility(false)
                        state.successMsg?.let { displayToast(it) }
                        if (state.isLoggedIn) {
                            setProgressVisibility(true)
                            startMainViewActivity()
                        }
                    }
                }
            }
        }
    }

    private fun bindButtons() {
        binding.accountLoginButton.setOnClickListener {
            showLoginDialog()
        }
        binding.anonymousLoginButton.setOnClickListener {
            authViewModel.registerAsGuest()
        }
        binding.createAccountButton.setOnClickListener {
            showCreateNewAccountDialog()
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

    private fun showLoginDialog() {
        val loginDialog = LoginDialog.newInstance()
        loginDialog.show(supportFragmentManager, "dialog")
    }

    private fun showCreateNewAccountDialog() {
        val createAccountDialog = CreateAccountDialog.newInstance()
        createAccountDialog.show(supportFragmentManager, "newAccountDialog")
    }

    private fun startMainViewActivity() {
        startActivity(Intent(this@LoginActivity, MainViewActivity::class.java))
        finish()
    }
}
