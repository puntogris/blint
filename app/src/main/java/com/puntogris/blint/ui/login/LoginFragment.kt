package com.puntogris.blint.ui.login

import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.puntogris.blint.R
import com.puntogris.blint.data.data_source.remote.LoginResult
import com.puntogris.blint.databinding.FragmentLoginBinding
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.SimpleResult
import com.puntogris.blint.utils.UiInterface
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>(R.layout.fragment_login) {

    @Inject
    lateinit var oneTapLogin: OneTapLogin
    private val viewModel: LoginViewModel by viewModels()
    private lateinit var loginActivityResultLauncher: ActivityResultLauncher<Intent>

    override fun initializeViews() {
        binding.fragment = this
        UiInterface.apply {
            registerUi(showAppBar = false, showToolbar = false)
        }
        registerActivityResultLauncher()
    }

    private fun registerActivityResultLauncher() {
        loginActivityResultLauncher =
                registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                   // onLoginFinished()
                    authGoogleUserIntoServer(it)
                }
    }

    private fun authGoogleUserIntoServer(result: ActivityResult) {
        lifecycleScope.launch {
            viewModel.authGoogleUser(result).collect(::handleAuthUserIntoServerResult)
        }
    }

    private fun handleAuthUserIntoServerResult(result: LoginResult) {
        when (result) {
            is LoginResult.Error -> {
                UiInterface.showSnackBar(
                        getString(R.string.snack_error_connection_server_try_later)
                )
               // onLoginError()
            }
            LoginResult.InProgress -> {
              //  onLoginStarted()
            }
            is LoginResult.Success -> {
                findNavController().navigate(R.id.mainFragment)

            }
        }
    }

    fun startLoginWithGoogle() {
     //   onLoginStarted()
        val intent = viewModel.getGoogleSignInIntent()
        loginActivityResultLauncher.launch(intent)
    }

    fun continueAnonymously() {
        lifecycleScope.launch {
            when (viewModel.registerAnonymousUser()) {
                SimpleResult.Failure -> {
                    UiInterface.showSnackBar(getString(R.string.snack_error_connection_server_try_later))
                 //   onLoginError()
                }
                SimpleResult.Success -> {
                    findNavController().navigate(R.id.welcomeFragment)
                }
            }
        }
    }

    fun onLoginProblemsClicked(){
        findNavController().navigate(R.id.action_loginFragment_to_loginProblemsFragment)
    }
}