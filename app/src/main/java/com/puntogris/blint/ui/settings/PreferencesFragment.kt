package com.puntogris.blint.ui.settings

import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.fragment.findNavController
import androidx.preference.ListPreference
import androidx.preference.Preference
import com.puntogris.blint.R
import com.puntogris.blint.ui.base.BasePreferences
import com.puntogris.blint.utils.getParentFab
import com.puntogris.blint.utils.setUpUi
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PreferencesFragment: BasePreferences(R.xml.preferences) {

    override fun initializeViews() {

        findPreference<Preference>("account_pref")?.setOnPreferenceClickListener {
            findNavController().navigate(R.id.accountPreferences)
            true
        }
        findPreference<Preference>("notifications_pref")?.setOnPreferenceClickListener {
            findNavController().navigate(R.id.notificationsPreferences)
            true
        }
        findPreference<Preference>("backup_pref")?.setOnPreferenceClickListener {
            findNavController().navigate(R.id.backUpPreferences)
            true
        }
        findPreference<ListPreference>("theme_pref")?.setOnPreferenceChangeListener { _, newValue ->
            AppCompatDelegate.setDefaultNightMode(Integer.parseInt(newValue.toString()))
            true
        }
        findPreference<Preference>("help_pref")?.setOnPreferenceClickListener {
            true
        }
        findPreference<Preference>("about_pref")?.setOnPreferenceClickListener {
            findNavController().navigate(R.id.aboutFragment)
            true
        }
        findPreference<Preference>("categories_pref")?.setOnPreferenceClickListener {
            findNavController().navigate(R.id.manageCategoriesFragment)
            true
        }
    }


}