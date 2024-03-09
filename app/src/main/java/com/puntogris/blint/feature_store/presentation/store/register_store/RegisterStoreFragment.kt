package com.puntogris.blint.feature_store.presentation.store.register_store

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.puntogris.blint.R
import com.puntogris.blint.common.presentation.base.BaseFragment
import com.puntogris.blint.common.utils.*
import com.puntogris.blint.common.utils.types.ProgressResource
import com.puntogris.blint.common.utils.types.StringValidator
import com.puntogris.blint.databinding.FragmentRegisterStoreBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterStoreFragment :
    BaseFragment<FragmentRegisterStoreBinding>(R.layout.fragment_register_store) {

    private val viewModel: RegisterStoreViewModel by viewModels()

    override fun initializeViews() {
        binding.fragment = this
        registerToolbarBackButton(binding.registerStoreToolbar)
    }

    fun onStartBusinessRegistration() {
        val validator = StringValidator.from(
            text = binding.registerStoreName.getString(),
            isName = true,
            maxLength = 25
        )
        when (validator) {
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
                        binding.registerStoreContinueButton.isEnabled = true
                        binding.registerStoreAnimationView.playAnimationOnce(R.raw.error)
                    }

                    is ProgressResource.Success -> {
                        UiInterface.showSnackBar(getString(R.string.snack_created_store_success))
                        findNavController().navigateAndClearStack(R.id.homeFragment)
                    }

                    ProgressResource.InProgress -> {
                        binding.registerStoreContinueButton.isEnabled = false
                        binding.registerStoreAnimationView.playAnimationOnce(R.raw.loading)
                    }
                }
            }
        }
    }
}
