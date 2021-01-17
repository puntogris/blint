package com.puntogris.blint.ui.settings

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import com.puntogris.blint.R
import com.puntogris.blint.ui.base.BasePreferences
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountPreferences: BasePreferences(R.xml.account_preferences) {

    private val viewModel: PreferencesViewModel by viewModels()

    override fun initializeViews() {

        findPreference<Preference>("user_data_pref")?.setOnPreferenceClickListener {
            findNavController().navigate(R.id.userAccountFragment)
            true
        }
        findPreference<Preference>("user_business_pref")?.setOnPreferenceClickListener {

            true
        }
        findPreference<Preference>("payments_pref")?.setOnPreferenceClickListener {

            true
        }
        findPreference<Preference>("privacy_pref")?.setOnPreferenceClickListener {

            true
        }

        findPreference<Preference>("singOut_pref")?.setOnPreferenceClickListener {
            viewModel.logOut()
            findNavController().navigate(R.id.loginFragment)
            true
        }
    }

}