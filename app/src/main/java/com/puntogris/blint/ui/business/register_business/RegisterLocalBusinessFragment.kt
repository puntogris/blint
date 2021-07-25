package com.puntogris.blint.ui.business.register_business

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentRegisterLocalBusinessBinding
import com.puntogris.blint.ui.SharedPref
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.ui.login.LoginViewModel
import com.puntogris.blint.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class RegisterLocalBusinessFragment : BaseFragment<FragmentRegisterLocalBusinessBinding>(R.layout.fragment_register_local_business) {

    private val viewModel: RegisterBusinessViewModel by viewModels()

    override fun initializeViews() {
        binding.registerLocalBusinessFragment = this
    }

    fun onEndRegistrationButtonClicked(){
        when(val validator = StringValidator.from(binding.businessNameText.getString())){
            is StringValidator.Valid -> {
                binding.continueButton.isEnabled = false
                binding.animationView.playAnimationOnce(R.raw.loading)
                lifecycleScope.launch {
                    when(viewModel.registerNewBusiness(validator.value)){
                        SimpleResult.Failure -> {
                            showShortSnackBar(getString(R.string.snack_error_connection_server_try_later))
                            binding.continueButton.isEnabled = true
                            binding.animationView.playAnimationOnce(R.raw.error)
                        }
                        SimpleResult.Success -> {
                            showSnackBarVisibilityAppBar(getString(R.string.snack_created_business_success))
                            val nav = NavOptions.Builder().setPopUpTo(R.id.navigation, true).build()
                            findNavController().navigate(R.id.mainFragment, null, nav)
                        }
                    }
                }
            }
            is StringValidator.NotValid -> showShortSnackBar(getString(validator.error))
        }
    }
}