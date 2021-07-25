package com.puntogris.blint.ui.settings

import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.SwitchPreference
import com.puntogris.blint.R
import com.puntogris.blint.ui.base.BasePreferences
import com.puntogris.blint.utils.Constants.AUTO_BACKUP_PREF
import com.puntogris.blint.utils.Constants.CREATE_BACKUP_PREF
import com.puntogris.blint.utils.Constants.RESTORE_BACKUP_PREF
import com.puntogris.blint.utils.registerUiInterface

class BackupPreferences:BasePreferences(R.xml.backup_preferences) {

    override fun initializeViews() {
        registerUiInterface.register()
        findPreference<SwitchPreference>(AUTO_BACKUP_PREF)?.setOnPreferenceClickListener {
            true
        }
        findPreference<Preference>(CREATE_BACKUP_PREF)?.setOnPreferenceClickListener {
            findNavController().navigate(R.id.createBackupFragment)
            true
        }
        findPreference<Preference>(RESTORE_BACKUP_PREF)?.setOnPreferenceClickListener {
            findNavController().navigate(R.id.restoreBackupFragment)
            true
        }
    }
}