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
import com.puntogris.blint.common.utils.types.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PreferencesFragment : PreferenceFragmentCompat() {

    private val viewModel: PreferencesViewModel by viewModels()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        UiInterface.registerUi(showAppBar = false)

        onPreferenceChange(Keys.THEME_PREF) {
            AppCompatDelegate.setDefaultNightMode(Integer.parseInt(it.toString()))
        }

        preference(Keys.SIGN_OUT_PREF) {
            onClick {
                lifecycleScope.launch {
                    when (viewModel.logOut()) {
                        is Resource.Error -> {
                            UiInterface.showSnackBar(getString(R.string.snack_an_error_occurred))
                        }
                        is Resource.Success -> {
                            findNavController().navigateAndClearStack(R.id.loginFragment)
                        }
                    }
                }
            }
        }
    }
}