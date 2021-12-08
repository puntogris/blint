package com.puntogris.blint.feature_store.presentation.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceFragmentCompat
import com.puntogris.blint.R
import com.puntogris.blint.common.utils.*
import com.puntogris.blint.common.utils.types.SimpleResult
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PreferencesFragment : PreferenceFragmentCompat() {

    private val viewModel: PreferencesViewModel by viewModels()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        UiInterface.registerUi(showAppBar = false)


        preferenceOnClick(Keys.ACCOUNT_PREF) {
            findNavController().navigate(R.id.userAccountFragment)
        }

        onPreferenceChange(Keys.THEME_PREF) {
            AppCompatDelegate.setDefaultNightMode(Integer.parseInt(it.toString()))
        }

        preferenceOnClick(Keys.TICKET_PREF) {
            findNavController().navigate(R.id.sendTicketFragment)
        }

        preference(Keys.SIGN_OUT_PREF) {
            onClick {
                lifecycleScope.launch {
                    when (viewModel.logOut()) {
                        SimpleResult.Failure -> {
                            UiInterface.showSnackBar(getString(R.string.snack_an_error_occurred))
                        }
                        SimpleResult.Success -> {
                            val nav = NavOptions.Builder().setPopUpTo(R.id.navigation, true).build()
                            findNavController().navigate(R.id.loginFragment, null, nav)
                        }
                    }
                }
            }
        }
    }
}