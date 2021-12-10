package com.puntogris.blint.feature_store.presentation.business.register_business

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.puntogris.blint.R
import com.puntogris.blint.common.presentation.base.BaseFragment
import com.puntogris.blint.common.utils.*
import com.puntogris.blint.common.utils.types.ProgressResource
import com.puntogris.blint.common.utils.types.StringValidator
import com.puntogris.blint.databinding.FragmentRegisterBusinessBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterBusinessFragment :
    BaseFragment<FragmentRegisterBusinessBinding>(R.layout.fragment_register_business) {

    private val viewModel: RegisterBusinessViewModel by viewModels()

    override fun initializeViews() {
        UiInterface.registerUi(showAppBar = false)
        binding.fragment = this
    }

    fun onStartBusinessRegistration() {
        when (val validator = StringValidator.from(
            binding.businessNameText.getString(),
            isName = true,
            maxLength = 25
        )) {
            is StringValidator.Valid -> registerBusiness(validator.value)
            is StringValidator.NotValid -> UiInterface.showSnackBar(getString(validator.error))
        }
    }

    private fun registerBusiness(name: String) {
        lifecycleScope.launch {
            viewModel.registerNewBusiness(name).collect {
                when (it) {
                    is ProgressResource.Error -> {
                        UiInterface.showSnackBar(getString(R.string.snack_error_connection_server_try_later))
                        binding.continueButton.isEnabled = true
                        binding.animationView.playAnimationOnce(R.raw.error)
                    }
                    is ProgressResource.Success -> {
                        UiInterface.showSnackBar(getString(R.string.snack_created_business_success))
                        findNavController().navigateAndClearStack(R.id.homeFragment)
                    }
                    ProgressResource.InProgress -> {
                        binding.continueButton.isEnabled = false
                        binding.animationView.playAnimationOnce(R.raw.loading)
                    }
                }
            }
        }
    }
}