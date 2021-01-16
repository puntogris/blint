package com.puntogris.blint.ui.business

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentWelcomeNewUserBinding
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.ui.login.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WelcomeNewUserFragment : BaseFragment<FragmentWelcomeNewUserBinding>(R.layout.fragment_welcome_new_user) {

    private val viewModel: LoginViewModel by viewModels()

    override fun initializeViews() {
        binding.alertRegisterFragment = this
    }

    fun onContinueButtonClicked(){
        findNavController().navigate(R.id.action_welcomeNewUserFragment_to_businessWaitingRoomFragment)
    }

    fun onExitButtonClicked(){
        viewModel.singOut()
        findNavController().navigate(R.id.loginFragment)
    }
}