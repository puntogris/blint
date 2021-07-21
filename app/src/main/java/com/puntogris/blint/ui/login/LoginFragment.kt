package com.puntogris.blint.ui.login

import android.animation.ObjectAnimator
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts.StartIntentSenderForResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.common.api.ApiException
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentLoginBinding
import com.puntogris.blint.model.Business
import com.puntogris.blint.model.Employee
import com.puntogris.blint.model.UserData
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>(R.layout.fragment_login) {

    @Inject
    lateinit var oneTapLogin: OneTapLogin
    private val viewModel: LoginViewModel by viewModels()
    private lateinit var activityResultLauncher: ActivityResultLauncher<IntentSenderRequest>

    override fun initializeViews() {
        binding.fragment = this
        setUpUi(showAppBar = false, showToolbar = false)
        setupStatusBarForLoginBackground()
        activityResultLauncher = registerForActivityResult(StartIntentSenderForResult()){ onLoginResult(it) }
    }

    fun onLoginButtonClicked(){
        ObjectAnimator
            .ofFloat(binding.textView118,"translationX", 0f, 25f, -25f, 25f, -25f,15f, -15f, 6f, -6f, 0f)
            .setDuration(800L)
            .start()

        if (oneTapLogin.isLoginEnabled()) oneTapLogin.showSingInUI(activityResultLauncher)
        else showShortSnackBar(getString(R.string.snack_login_warning))
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
            when(val data = viewModel.lookUpUserBusinessData(result.user)){
                is RegistrationData.Complete -> {
                    binding.logInProgressBar.gone()
                    val action = LoginFragmentDirections.actionLoginFragmentToWelcomeFragment(showIntro = false, data.userData)
                    findNavController().navigate(action)
                }
                RegistrationData.Error -> {
                    binding.logInProgressBar.gone()
                    showShortSnackBar(getString(R.string.snack_error_connection_server_try_later))
                }
                RegistrationData.Incomplete -> {
                    val action = LoginFragmentDirections.actionLoginFragmentToWelcomeFragment(showIntro = true)
                    findNavController().navigate(action)
                }
                RegistrationData.NotFound -> {
                    val action = LoginFragmentDirections.actionLoginFragmentToWelcomeFragment(showIntro = true)
                    findNavController().navigate(action)
                }
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