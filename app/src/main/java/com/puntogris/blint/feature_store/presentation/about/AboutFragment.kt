package com.puntogris.blint.feature_store.presentation.about

import com.puntogris.blint.R
import com.puntogris.blint.common.presentation.base.BaseFragment
import com.puntogris.blint.common.utils.Constants.APP_PLAY_STORE_URI
import com.puntogris.blint.common.utils.Constants.PLAY_STORE_PACKAGE
import com.puntogris.blint.common.utils.Constants.PRIVACY_POLICY_URI
import com.puntogris.blint.common.utils.Constants.TERMS_AND_CONDITIONS_URI
import com.puntogris.blint.common.utils.UiInterface
import com.puntogris.blint.common.utils.launchWebBrowserIntent
import com.puntogris.blint.databinding.FragmentAboutBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AboutFragment : BaseFragment<FragmentAboutBinding>(R.layout.fragment_about) {


    override fun initializeViews() {
        UiInterface.registerUi(showAppBar = false)
        binding.fragment = this
    }

    fun onRateAppClicked() {
        launchWebBrowserIntent(APP_PLAY_STORE_URI, PLAY_STORE_PACKAGE)
    }

    fun onPrivacyPolicyClicked() {
        launchWebBrowserIntent(PRIVACY_POLICY_URI)
    }

    fun onTermsAndConditionsClicked() {
        launchWebBrowserIntent(TERMS_AND_CONDITIONS_URI)
    }

}