package com.puntogris.blint.ui.login

import android.content.Intent
import androidx.core.view.setPadding
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.firebase.auth.FirebaseAuth
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentLoginBinding
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.AuthResult
import com.puntogris.blint.utils.Constants.RC_ONE_TAP
import com.puntogris.blint.utils.createShortSnackBar
import com.puntogris.blint.utils.showShortSnackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>(R.layout.fragment_login) {

    @Inject
    lateinit var oneTapLogin: OneTapLogin
    private val viewModel: LoginViewModel by viewModels()

    override fun initializeViews() {
        binding.loginFragment = this
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            RC_ONE_TAP -> {
                try {
                    oneTapLogin.getSingInCredentials(data).googleIdToken?.let { credentialToken ->
                        lifecycleScope.launchWhenCreated {
                            viewModel.logInUserToFirestore(credentialToken).collect { result ->
                                when (result) {
                                    is AuthResult.Success -> {
                                        viewModel.saveUserToDatabase(result.user)
                                        navigateToMainApp()
                                    }
                                    is AuthResult.Error -> {
                                        showShortSnackBar(result.exception)
                                    }
                                }
                            }
                        }
                    }
                } catch (e: ApiException) {
                    when (e.statusCode) {
                        CommonStatusCodes.CANCELED -> {
                            oneTapLogin.loginCanceled()
                        }
                        CommonStatusCodes.NETWORK_ERROR ->
                            showShortSnackBar("Se encontro un problema con la conexion. Revisa tu red y intenta nuevamente.")
                        else ->
                            showShortSnackBar("Estamos teniendo dificultades tecnicas. Intenta nuevamente en un rato.")
                    }
                }
            }
        }
    }

    fun onLoginButtonClicked(){
        if (oneTapLogin.isEnabled()) oneTapLogin.singIn(this)
        else showShortSnackBar("Debido a multiples intentos de ingreso consecutivos, se deshabilito el ingreso a la app momentaneamente.")
    }

    fun onLoginProblemsClicked(){
        findNavController().navigate(R.id.action_loginFragment_to_loginProblemsFragment)
    }

    private fun navigateToMainApp(){
        lifecycleScope.launch {
            if (viewModel.userHasBusinessRegistered())
                findNavController().navigate(R.id.mainFragment)
            else
                findNavController().navigate(R.id.alertRegisterBusinessFragment)
        }
    }
}