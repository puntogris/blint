package com.puntogris.blint.ui.about

import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentAboutBinding
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.Constants.APP_PLAY_STORE_URI
import com.puntogris.blint.utils.Constants.PLAY_STORE_PACKAGE
import com.puntogris.blint.utils.Constants.PRIVACY_POLICY_URI
import com.puntogris.blint.utils.Constants.TERMS_AND_CONDITIONS_URI
import com.puntogris.blint.utils.UiInterface
import com.puntogris.blint.utils.launchWebBrowserIntent
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