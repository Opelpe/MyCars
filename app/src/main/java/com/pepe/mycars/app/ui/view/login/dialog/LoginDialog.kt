package com.pepe.mycars.app.ui.view.login.dialog

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.text.method.TransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.pepe.mycars.app.utils.ColorUtils
import com.pepe.mycars.app.utils.state.view.LoginViewState
import com.pepe.mycars.app.viewmodel.AuthViewModel
import com.pepe.mycars.databinding.DialogLoginBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginDialog : DialogFragment() {
    private lateinit var binding: DialogLoginBinding
    private lateinit var launcher: ActivityResultLauncher<Intent>
    private val authModel: AuthViewModel by activityViewModels()

    companion object {
        fun newInstance(autoLogin: Boolean): LoginDialog {
            val dialog = LoginDialog()
            val args = Bundle()
            args.putBoolean("autoLogin", autoLogin)
            dialog.arguments = args
            return dialog
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DialogLoginBinding.inflate(layoutInflater, container, false)

        launcher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = result.data
                    val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                    signInWithGoogle(task)
                }
            }

        dialog?.let { it.window?.requestFeature(Window.FEATURE_NO_TITLE) }

        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        setupButtons()
    }

    private fun setupButtons() {
        binding.passwordVisibilityIcon.imageTintList = ColorUtils(requireContext()).getImageColorStateList()
        binding.passwordVisibilityIcon.setOnClickListener {
            setPasswordVisibility()
        }

        binding.submitButton.setOnClickListener {
            val email = binding.emailInput.getText().toString()
            val password = binding.passwordInput.getText().toString()
            onSubmitBtnClicked(email, password)
        }

        binding.googleLoginButton.setOnClickListener {
            onGoogleBtnClicked()
        }
    }

    override fun onStart() {
        super.onStart()
        dialog ?: return
        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val height = ViewGroup.LayoutParams.WRAP_CONTENT
        dialog?.window?.setLayout(width, height)
        observeAuthState()
    }

    private fun observeAuthState() {
        authModel.loginViewState.observe(this) {
            when (it) {
                LoginViewState.Loading -> setProgressVisibility(true)
                is LoginViewState.Error -> {
                    if (it.errorMsg.isNotBlank()) {
                        setProgressVisibility(false)
                    }
                }
                is LoginViewState.Success -> {
                    setProgressVisibility(false)
                }
            }
        }
    }

    private fun onSubmitBtnClicked(
        email: String?,
        password: String?,
    ) {
        val autoLogin = arguments?.getBoolean("autoLogin") ?: false
        authModel.login(email, password, autoLogin)
    }

    private fun onGoogleBtnClicked() {
        val gso =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(requireContext().getString(com.firebase.ui.auth.R.string.default_web_client_id))
                .requestEmail()
                .build()

        val googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
        googleSignInClient.signOut()
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    private fun signInWithGoogle(task: Task<GoogleSignInAccount>) {
        val result = task.result ?: return
        val token = result.idToken ?: return
        val name = result.displayName ?: ""
        val email = result.email ?: ""

        authModel.signInWithGoogle(
            idToken = token,
            userName = name,
            email = email,
        )
    }

    private fun setProgressVisibility(loading: Boolean) {
        if (loading) {
            dialog!!.window!!.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            )
            binding.progressView.visibility = View.VISIBLE
        } else {
            binding.progressView.visibility = View.GONE
            dialog!!.window!!.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }
    }

    private fun setPasswordVisibility() {
        val transformationMethod: TransformationMethod = binding.passwordInput.transformationMethod
        if (transformationMethod == PasswordTransformationMethod.getInstance()) {
            transformationHide()
        }
        if (transformationMethod == HideReturnsTransformationMethod.getInstance()) {
            transformationShow()
        }
    }

    private fun transformationHide() {
        val passwordInput = binding.passwordInput
        val hide = HideReturnsTransformationMethod.getInstance()
        binding.passwordVisibilityIcon.setImageResource(com.pepe.mycars.R.drawable.ic_password_visibility_off)
        passwordInput.transformationMethod = hide
        passwordInput.setSelection(passwordInput.getText().length)
    }

    private fun transformationShow() {
        val passwordInput = binding.passwordInput
        val show = PasswordTransformationMethod.getInstance()
        binding.passwordVisibilityIcon.setImageResource(com.pepe.mycars.R.drawable.ic_password_visibility)
        passwordInput.transformationMethod = show
        passwordInput.setSelection(passwordInput.getText().length)
    }
}
