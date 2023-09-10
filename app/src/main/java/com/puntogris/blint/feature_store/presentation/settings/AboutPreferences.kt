package com.puntogris.blint.feature_store.presentation.settings

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.preference.PreferenceFragmentCompat
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.google.android.material.appbar.MaterialToolbar
import com.puntogris.blint.BuildConfig
import com.puntogris.blint.R
import com.puntogris.blint.common.utils.Keys
import com.puntogris.blint.common.utils.preference
import com.puntogris.blint.common.utils.preferenceOnClick
import com.puntogris.blint.common.utils.registerToolbarBackButton

class AboutPreferences : PreferenceFragmentCompat() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.about_preferences)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<MaterialToolbar>(R.id.preferences_toolbar).apply {
            registerToolbarBackButton(this)
            setTitle(R.string.about_us_label)
        }


        preferenceOnClick(Keys.LICENSES_PREF) {
            Intent(requireContext(), OssLicensesMenuActivity::class.java).apply {
                OssLicensesMenuActivity.setActivityTitle(getString(R.string.open_source_licenses))
                startActivity(this)
            }
        }

        preference(Keys.APP_VERSION_PREF) {
            summary = BuildConfig.VERSION_NAME + " (${BuildConfig.VERSION_CODE})"
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) = Unit
}
