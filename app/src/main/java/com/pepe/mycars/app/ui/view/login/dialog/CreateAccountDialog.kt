package com.pepe.mycars.app.ui.view.login.dialog

import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.text.method.TransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.coroutineScope
import com.pepe.mycars.app.utils.ColorUtils
import com.pepe.mycars.app.utils.displayToast
import com.pepe.mycars.app.utils.networkState.AuthState
import com.pepe.mycars.app.viewmodel.AuthViewModel
import com.pepe.mycars.databinding.DialogCreateAccountBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CreateAccountDialog: DialogFragment() {

    private lateinit var binding: DialogCreateAccountBinding

    private val authModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogCreateAccountBinding.inflate(layoutInflater, container, false)

        return binding.root
    }

    companion object {
        fun newInstance(autoLogin: Boolean): CreateAccountDialog {
            val f = CreateAccountDialog()
            val args = Bundle()
            args.putBoolean("autoLogin", autoLogin)
            f.arguments = args
            return f
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeAuthState()
        setupButtons()
        setupTextsColor()
    }

    private fun setupTextsColor() {
        binding.cancelButtonText.setTextColor(ColorUtils(requireContext()).getButtonPrimeColorStateList())
        binding.passwordVisibilityIcon.imageTintList = ColorUtils(requireContext()).getButtonSecondColorStateList()

        binding.userNameEditText.onFocusChangeListener =
            View.OnFocusChangeListener { _: View?, b: Boolean ->
                if (b) {
                    binding.userNameTitle.setTextColor(
                        resources.getColor(
                            com.pepe.mycars.R.color.green,
                            activity?.theme
                        )
                    )
                } else {
                    binding.userNameTitle.setTextColor(
                        resources.getColor(
                            com.pepe.mycars.R.color.grey,
                            activity?.theme
                        )
                    )
                }
            }

        binding.emailEditText.onFocusChangeListener =
            View.OnFocusChangeListener { _: View?, b: Boolean ->
                if (b) {
                    binding.emailAddressTitle.setTextColor(
                        resources.getColor(
                            com.pepe.mycars.R.color.green,
                            activity?.theme
                        )
                    )
                } else {
                    binding.emailAddressTitle.setTextColor(
                        resources.getColor(
                            com.pepe.mycars.R.color.grey,
                            activity?.theme
                        )
                    )
                }
            }

        binding.passwordEditText.onFocusChangeListener =
            View.OnFocusChangeListener { _: View?, b: Boolean ->
                if (b) {
                    binding.passwordTitle.setTextColor(
                        resources.getColor(
                            com.pepe.mycars.R.color.green,
                            activity?.theme
                        )
                    )
                } else {
                    binding.passwordTitle.setTextColor(
                        resources.getColor(
                            com.pepe.mycars.R.color.grey,
                            activity?.theme
                        )
                    )
                }
            }
    }

    private fun setupButtons() {
        binding.createUserButton.setOnClickListener {
            val email = binding.emailEditText.getText().toString()
            val password = binding.passwordEditText.getText().toString()
            val name = binding.userNameEditText.getText().toString()
            createNewUserClicked(email, password, name)
        }

        binding.cancelButton.setOnClickListener {
            dismiss()
        }

        binding.passwordVisibilityIcon.setOnClickListener {
            setPasswordVisibility()
        }
    }

    private fun observeAuthState() {
        authModel.authState.observe(this) {
            when (it) {
                AuthState.Loading -> setProgressVisibility(true)
                is AuthState.Error -> {
                    if (it.errorMsg.isNotBlank()) {
                        setProgressVisibility(false)
                    }
                }
                is AuthState.Success -> {
                    setProgressVisibility(false)
                }
            }
        }
    }

    private fun setPasswordVisibility() {
        val transformationMethod: TransformationMethod =
            binding.passwordEditText.getTransformationMethod()
        if (transformationMethod == PasswordTransformationMethod.getInstance()) {
            transformationHide()
        }
        if (transformationMethod == HideReturnsTransformationMethod.getInstance()) {
            transformationShow()
        }
    }

    private fun transformationHide() {
        val passwordInput = binding.passwordEditText
        val hide = HideReturnsTransformationMethod.getInstance()
        binding.passwordVisibilityIcon.setImageResource(com.pepe.mycars.R.drawable.ic_password_visibility_off)
        passwordInput.transformationMethod = hide
        passwordInput.setSelection(passwordInput.getText().length)
    }

    private fun transformationShow() {
        val passwordInput = binding.passwordEditText
        val show = PasswordTransformationMethod.getInstance()
        binding.passwordVisibilityIcon.setImageResource(com.pepe.mycars.R.drawable.ic_password_visibility)
        passwordInput.transformationMethod = show
        passwordInput.setSelection(passwordInput.getText().length)
    }

    private fun createNewUserClicked(email: String?, password: String?, name: String?) {
        val autoLogin = arguments?.getBoolean("autoLogin") ?: false
        authModel.register(email, password, name, autoLogin)
    }

    private fun setProgressVisibility(loading: Boolean) {
        if (loading) {
            dialog!!.window!!.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
            binding.progressView.visibility = View.VISIBLE
        } else {
            binding.progressView.visibility = View.GONE
            dialog!!.window!!.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }
    }
}