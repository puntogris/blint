package com.puntogris.blint.feature_store.presentation.settings

import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import com.puntogris.blint.R
import com.puntogris.blint.common.presentation.base.BasePreferences
import com.puntogris.blint.common.utils.Constants.CREATE_BACKUP_PREF
import com.puntogris.blint.common.utils.Constants.RESTORE_BACKUP_PREF
import com.puntogris.blint.common.utils.UiInterface

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