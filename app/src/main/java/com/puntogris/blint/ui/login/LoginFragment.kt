package com.puntogris.blint.ui.login

import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts.StartIntentSenderForResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.common.api.ApiException
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentLoginBinding
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>(R.layout.fragment_login) {

    @Inject lateinit var oneTapLogin: OneTapLogin
    private val viewModel: LoginViewModel by viewModels()
    private lateinit var activityResultLauncher: ActivityResultLauncher<IntentSenderRequest>

    override fun initializeViews() {
        binding.loginFragment = this
        activityResultLauncher = registerForActivityResult(StartIntentSenderForResult()){ onLoginResult(it) }
    }

    fun onLoginButtonClicked(){
        oneTapLogin.showSingInUI(activityResultLauncher)
    }

    fun onLoginProblemsClicked(){
        findNavController().navigate(R.id.action_loginFragment_to_loginProblemsFragment)
    }

    private fun onLoginResult(activityResult: ActivityResult){
        try {
            oneTapLogin.getSingInCredentials(activityResult.data).googleIdToken?.let { credentialToken ->
                lifecycleScope.launch {
                    viewModel.logInUserToFirestore(credentialToken).collect { result ->
                        when (result) {
                            AuthResult.InProgress -> binding.logInProgressBar.visible()
                            is AuthResult.Success -> onSuccessLogIn(result)
                            is AuthResult.Error -> onErrorLogIn(result)
                        }
                    }
                }
            }
        } catch (e: ApiException) { oneTapLogin.onOneTapException(e) }
    }

    private fun onSuccessLogIn(result: AuthResult.Success){
        lifecycleScope.launch {
            when(viewModel.lookUpUserBusinessData(result.user)){
                BusinessData.Exists -> findNavController().navigate(R.id.mainFragment)
                BusinessData.NotExists -> findNavController().navigate(R.id.action_loginFragment_to_welcomeNewUserFragment)

                is BusinessData.Error -> showShortSnackBar("Error buscando tu informacion en los servidores.")
            }
        }
    }

    private fun onErrorLogIn(result: AuthResult.Error) {
        binding.logInProgressBar.gone()
        result.exception.localizedMessage?.let {
            showShortSnackBar(it)
        }
    }
}