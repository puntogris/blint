package com.puntogris.blint.ui.settings

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import com.puntogris.blint.R
import com.puntogris.blint.ui.base.BasePreferences
import com.puntogris.blint.utils.Constants.SIGN_OUT_PREF
import com.puntogris.blint.utils.Constants.USER_BUSINESS_PREF
import com.puntogris.blint.utils.Constants.USER_DATA_PREF
import com.puntogris.blint.utils.UiInterface
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AccountPreferences: BasePreferences(R.xml.account_preferences) {

    private val viewModel: PreferencesViewModel by viewModels()

    override fun initializeViews() {
        UiInterface.register()
        findPreference<Preference>(USER_DATA_PREF)?.setOnPreferenceClickListener {
            findNavController().navigate(R.id.userAccountFragment)
            true
        }
        findPreference<Preference>(USER_BUSINESS_PREF)?.setOnPreferenceClickListener {
            findNavController().navigate(R.id.manageBusinessFragment)
            true
        }
//        findPreference<Preference>(PAYMENTS_PREF)?.setOnPreferenceClickListener {
//
//            true
//        }
//        findPreference<Preference>(PRIVACY_PREF)?.setOnPreferenceClickListener {
//
//            true
//        }

        findPreference<Preference>(SIGN_OUT_PREF)?.setOnPreferenceClickListener {
            lifecycleScope.launch {
                viewModel.logOut()
                val nav = NavOptions.Builder().setPopUpTo(R.id.navigation, true).build()
                findNavController().navigate(R.id.loginFragment, null, nav)
            }
            true
        }
    }

}