package com.puntogris.blint.feature_store.presentation.settings

import android.content.Intent
import android.os.Bundle
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceFragmentCompat
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.puntogris.blint.R
import com.puntogris.blint.common.utils.Keys
import com.puntogris.blint.common.utils.UiInterface
import com.puntogris.blint.common.utils.preferenceOnClick

class AboutPreferences: PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.about_preferences, rootKey)
        UiInterface.registerUi(showAppBar = false)

        preferenceOnClick(Keys.LICENSES_PREF){
            Intent(requireContext(), OssLicensesMenuActivity::class.java).apply {
                OssLicensesMenuActivity.setActivityTitle(getString(R.string.open_source_licenses))
                startActivity(this)
            }
        }
    }
}