package com.puntogris.blint.ui.account

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentUserAccountBinding
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.ui.settings.PreferencesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserAccountFragment : BaseFragment<FragmentUserAccountBinding>(R.layout.fragment_user_account) {

    private val viewModel:PreferencesViewModel by viewModels()

    override fun initializeViews() {
        binding.fragment = this
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
    }

    fun onReadPrivacyPolicyClicked(){
        findNavController().navigate(R.id.privacyPolicyFragment)
    }
}