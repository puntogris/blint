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

        val toolbar = view.findViewById<MaterialToolbar>(R.id.preference_toolbar)
        toolbar.apply {
            registerToolbarBackButton(this)
            setTitle(R.string.backup_pref)
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {

        preferenceOnClick(Keys.CREATE_BACKUP_PREF) {
            findNavController().navigate(R.id.createBackupFragment)
        }

        preferenceOnClick(Keys.RESTORE_BACKUP_PREF) {
            findNavController().navigate(R.id.restoreBackupFragment)
        }
    }
}