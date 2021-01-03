package com.puntogris.blint.ui

import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentRegisterBusinessBinding
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.ui.main.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterBusiness : BaseFragment<FragmentRegisterBusinessBinding>(R.layout.fragment_register_business) {

    private val viewModel: MainViewModel by activityViewModels()

    override fun initializeViews() {
        binding.button3.setOnClickListener {
            lifecycleScope.launch {
                viewModel.registerNewBusiness(binding.editTextTextPersonName.text.toString())
                findNavController().navigate(R.id.mainFragment)
            }
        }
    }
}