package com.puntogris.blint.ui.business

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentAlertRegisterBusinessBinding
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.ui.login.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AlertRegisterBusinessFragment : BaseFragment<FragmentAlertRegisterBusinessBinding>(R.layout.fragment_alert_register_business) {

    private val viewModel: LoginViewModel by viewModels()

    override fun initializeViews() {
        binding.alertRegisterFragment = this
    }

    fun onContinueButtonClicked(){
        findNavController().navigate(R.id.action_alertRegisterBusinessFragment_to_registerBusinessFragment)
    }

    fun onExitButtonClicked(){
        viewModel.singOut()
        findNavController().navigate(R.id.action_alertRegisterBusinessFragment_to_loginFragment)
    }
}