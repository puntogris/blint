package com.puntogris.blint.feature_auth.presentation

import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.puntogris.blint.R
import com.puntogris.blint.common.presentation.base.BaseFragment
import com.puntogris.blint.common.utils.*
import com.puntogris.blint.databinding.FragmentLoginBinding
import com.puntogris.blint.feature_store.data.data_source.remote.LoginResult
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>(R.layout.fragment_login) {

    private val viewModel: LoginViewModel by viewModels()
    private lateinit var googleLoginLauncher: ActivityResultLauncher<Intent>

    override fun initializeViews() {
        binding.fragment = this
        registerGoogleLoginLauncher()
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
                        UiInterface.showSnackBar(getString(R.string.snack_error_connection_server_try_later))
                        binding.loginProgressBar.gone()
                    }
                    LoginResult.InProgress -> {
                        binding.loginProgressBar.visible()
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