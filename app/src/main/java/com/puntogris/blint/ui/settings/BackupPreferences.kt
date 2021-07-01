package com.puntogris.blint.ui.settings

import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.SwitchPreference
import com.puntogris.blint.R
import com.puntogris.blint.ui.base.BasePreferences
import com.puntogris.blint.utils.getParentFab

class BackupPreferences:BasePreferences(R.xml.backup_preferences) {

    override fun initializeViews() {
        getParentFab().hide()
        findPreference<SwitchPreference>("auto_backup_pref")?.setOnPreferenceClickListener {
            true
        }
        findPreference<Preference>("create_backup_pref")?.setOnPreferenceClickListener {
            findNavController().navigate(R.id.createBackupFragment)
            true
        }
        findPreference<Preference>("restore_backup_pref")?.setOnPreferenceClickListener {
            findNavController().navigate(R.id.restoreBackupFragment)
            true
        }
    }
}