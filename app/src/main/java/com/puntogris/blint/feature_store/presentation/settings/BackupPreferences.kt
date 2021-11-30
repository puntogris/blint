package com.puntogris.blint.feature_store.presentation.settings

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceFragmentCompat
import com.puntogris.blint.R
import com.puntogris.blint.common.utils.Keys
import com.puntogris.blint.common.utils.UiInterface
import com.puntogris.blint.common.utils.preferenceOnClick

class BackupPreferences : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.backup_preferences, rootKey)

        UiInterface.registerUi(showAppBar = false)

        preferenceOnClick(Keys.CREATE_BACKUP_PREF) {
            findNavController().navigate(R.id.createBackupFragment)
        }

        preferenceOnClick(Keys.RESTORE_BACKUP_PREF) {
            findNavController().navigate(R.id.restoreBackupFragment)
        }
    }
}