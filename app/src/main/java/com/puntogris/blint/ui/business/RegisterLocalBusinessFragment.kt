package com.puntogris.blint.ui.business

import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentRegisterLocalBusinessBinding
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.ui.main.MainViewModel
import com.puntogris.blint.utils.*
import kotlinx.coroutines.launch

class RegisterLocalBusinessFragment : BaseFragment<FragmentRegisterLocalBusinessBinding>(R.layout.fragment_register_local_business) {

    private val viewModel: MainViewModel by activityViewModels()

        override fun initializeViews() {
            getParentFab().apply {
                isEnabled = true
                changeIconFromDrawable(R.drawable.ic_baseline_save_24)
                setOnClickListener {
                    when(val validator = StringValidator.from(binding.businessName.getString(), allowSpecialChars = false)){
                        is StringValidator.Valid ->
                            lifecycleScope.launch {
                                viewModel.registerNewBusiness(validator.value)
                                findNavController().navigate(R.id.mainFragment)
                            }
                        is StringValidator.NotValid -> showLongSnackBarAboveFab(validator.error)
                    }
                }
            }
        }
    }