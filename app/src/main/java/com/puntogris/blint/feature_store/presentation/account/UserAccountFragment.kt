package com.puntogris.blint.feature_store.presentation.account

import androidx.fragment.app.viewModels
import com.puntogris.blint.R
import com.puntogris.blint.common.presentation.base.BaseFragment
import com.puntogris.blint.common.utils.Constants.PRIVACY_POLICY_URI
import com.puntogris.blint.common.utils.launchWebBrowserIntent
import com.puntogris.blint.databinding.FragmentUserAccountBinding
import com.puntogris.blint.feature_store.presentation.settings.PreferencesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserAccountFragment :
    BaseFragment<FragmentUserAccountBinding>(R.layout.fragment_user_account) {

    private val viewModel: PreferencesViewModel by viewModels()

    override fun initializeViews() {
        binding.fragment = this
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
    }

    fun onReadPrivacyPolicyClicked() {
        launchWebBrowserIntent(PRIVACY_POLICY_URI)
    }
}