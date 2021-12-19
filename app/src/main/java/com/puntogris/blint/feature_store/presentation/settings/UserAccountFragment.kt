package com.puntogris.blint.feature_store.presentation.settings

import androidx.fragment.app.viewModels
import com.puntogris.blint.R
import com.puntogris.blint.common.presentation.base.BaseFragment
import com.puntogris.blint.common.utils.registerToolbarBackButton
import com.puntogris.blint.databinding.FragmentUserAccountBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserAccountFragment :
    BaseFragment<FragmentUserAccountBinding>(R.layout.fragment_user_account) {

    private val viewModel: PreferencesViewModel by viewModels()

    override fun initializeViews() {
        binding.fragment = this
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        registerToolbarBackButton(binding.toolbar)
    }
}