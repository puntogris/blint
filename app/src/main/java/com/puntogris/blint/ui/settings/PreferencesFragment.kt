package com.puntogris.blint.ui.settings

import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.fragment.findNavController
import androidx.preference.ListPreference
import androidx.preference.Preference
import com.puntogris.blint.R
import com.puntogris.blint.ui.base.BasePreferences
import com.puntogris.blint.utils.Constants.ABOUT_PREF
import com.puntogris.blint.utils.Constants.ACCOUNT_PREF
import com.puntogris.blint.utils.Constants.BACKUP_PREF
import com.puntogris.blint.utils.Constants.HELP_PREF
import com.puntogris.blint.utils.Constants.NOTIFICATIONS_PREF
import com.puntogris.blint.utils.Constants.THEME_PREF
import com.puntogris.blint.utils.UiInterface
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PreferencesFragment: BasePreferences(R.xml.preferences) {

    override fun initializeViews() {
        UiInterface.register()
        findPreference<Preference>(ACCOUNT_PREF)?.setOnPreferenceClickListener {
            findNavController().navigate(R.id.accountPreferences)
            true
        }
        findPreference<Preference>(NOTIFICATIONS_PREF)?.setOnPreferenceClickListener {
            findNavController().navigate(R.id.notificationsPreferences)
            true
        }
        findPreference<Preference>(BACKUP_PREF)?.setOnPreferenceClickListener {
            findNavController().navigate(R.id.backUpPreferences)
            true
        }
        findPreference<ListPreference>(THEME_PREF)?.setOnPreferenceChangeListener { _, newValue ->
            AppCompatDelegate.setDefaultNightMode(Integer.parseInt(newValue.toString()))
            true
        }
        findPreference<Preference>(HELP_PREF)?.setOnPreferenceClickListener {
            findNavController().navigate(R.id.sendTicketFragment)
            true
        }
        findPreference<Preference>(ABOUT_PREF)?.setOnPreferenceClickListener {
            findNavController().navigate(R.id.aboutFragment)
            true
        }
    }

}