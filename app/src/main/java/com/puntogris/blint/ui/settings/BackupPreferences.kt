package com.puntogris.blint.ui.settings

import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import com.puntogris.blint.R
import com.puntogris.blint.ui.base.BasePreferences
import com.puntogris.blint.utils.Constants.CREATE_BACKUP_PREF
import com.puntogris.blint.utils.Constants.RESTORE_BACKUP_PREF
import com.puntogris.blint.utils.UiInterface

class BackupPreferences : BasePreferences(R.xml.backup_preferences) {

    override fun initializeViews() {
        UiInterface.registerUi()
//        findPreference<SwitchPreference>(AUTO_BACKUP_PREF)?.setOnPreferenceClickListener {
//            true
//        }
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