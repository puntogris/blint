package com.puntogris.blint.feature_auth.presentation

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.puntogris.blint.R
import com.puntogris.blint.common.utils.Constants
import com.puntogris.blint.common.utils.UiInterface
import com.puntogris.blint.common.utils.gone
import com.puntogris.blint.common.utils.launchWebBrowserIntent
import com.puntogris.blint.common.utils.viewBinding
import com.puntogris.blint.common.utils.visible
import com.puntogris.blint.databinding.FragmentLoginBinding
import com.puntogris.blint.feature_store.data.data_source.remote.LoginResult
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {

    private val viewModel: LoginViewModel by viewModels()
    private lateinit var googleLoginLauncher: ActivityResultLauncher<Intent>

    private val binding by viewBinding(FragmentLoginBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerGoogleLoginLauncher()
        setupListeners()
    }

    private fun setupListeners() {
        binding.buttonGoogleLogin.setOnClickListener {
            startLoginWithGoogle()
        }
        binding.buttonLoginProblems.setOnClickListener {
            onLoginProblemsClicked()
        }
    }

    private fun registerGoogleLoginLauncher() {
        googleLoginLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                authGoogleUserIntoServer(it)
            }
    }

    private fun authGoogleUserIntoServer(result: ActivityResult) {
        lifecycleScope.launch {
            viewModel.authGoogleUser(result).collect {
                when (it) {
                    is LoginResult.Error -> {
                        UiInterface.showSnackBar(
                            getString(R.string.snack_error_connection_server_try_later)
                        )
                        binding.progressBarLogin.gone()
                    }

                    LoginResult.InProgress -> {
                        binding.progressBarLogin.visible()
                    }

                    is LoginResult.Success -> {
                        val action =
                            LoginFragmentDirections.actionLoginFragmentToSyncAccountFragment(it.authUser)
                        findNavController().navigate(action)
                    }
                }
            }
        }
    }

    fun startLoginWithGoogle() {
        val intent = viewModel.getGoogleSignInIntent()
        googleLoginLauncher.launch(intent)
    }

    fun onLoginProblemsClicked() {
        launchWebBrowserIntent(Constants.LOGIN_PROBLEMS_URL)
    }
}