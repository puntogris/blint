package com.puntogris.blint.ui.business

import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentRegisterLocalBusinessBinding
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.ui.main.MainViewModel
import com.puntogris.blint.utils.StringValidator
import com.puntogris.blint.utils.createShortSnackBar
import com.puntogris.blint.utils.getString
import com.puntogris.blint.utils.showShortSnackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterLocalBusinessFragment : BaseFragment<FragmentRegisterLocalBusinessBinding>(R.layout.fragment_register_local_business) {

    private val viewModel: MainViewModel by activityViewModels()

    override fun initializeViews() {
        binding.registerLocalBusinessFragment = this
    }

    fun onEndRegistrationButtonClicked(){
        when(val validator = StringValidator.from(binding.businessNameText.getString())){
            is StringValidator.Valid -> {
                lifecycleScope.launch {
                    viewModel.registerNewBusiness(validator.value)
                    findNavController().navigate(R.id.mainFragment)
                }
            }
            is StringValidator.NotValid -> showShortSnackBar(validator.error)

        }
    }
}