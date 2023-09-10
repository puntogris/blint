package com.puntogris.blint.feature_store.presentation.settings

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceFragmentCompat
import com.google.android.material.appbar.MaterialToolbar
import com.puntogris.blint.R
import com.puntogris.blint.common.utils.Keys
import com.puntogris.blint.common.utils.preferenceOnClick
import com.puntogris.blint.common.utils.registerToolbarBackButton

class BackupPreferences : PreferenceFragmentCompat() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.backup_preferences)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<MaterialToolbar>(R.id.preferences_toolbar).apply {
            registerToolbarBackButton(this)
            setTitle(R.string.backup_pref)
        }

        preferenceOnClick(Keys.CREATE_BACKUP_PREF) {
            findNavController().navigate(R.id.createBackupFragment)
        }

        preferenceOnClick(Keys.RESTORE_BACKUP_PREF) {
            findNavController().navigate(R.id.restoreBackupFragment)
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) = Unit
}
