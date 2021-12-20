package com.puntogris.blint.feature_store.presentation.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceFragmentCompat
import com.google.android.material.appbar.MaterialToolbar
import com.puntogris.blint.BuildConfig
import com.puntogris.blint.R
import com.puntogris.blint.common.utils.*
import com.puntogris.blint.common.utils.types.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PreferencesFragment : PreferenceFragmentCompat() {

    private val viewModel: PreferencesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.preferences)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<MaterialToolbar>(R.id.preference_toolbar).apply {
            registerToolbarBackButton(this)
            setTitle(R.string.settings_label)
        }

        onPreferenceChange(Keys.THEME_PREF) {
            AppCompatDelegate.setDefaultNightMode(Integer.parseInt(it.toString()))
        }

        preferenceOnClick(Keys.TICKET_PREFERENCE) {
            val email = Uri.encode("puntogrishelp@gmail.com")
            val subject = Uri.encode("Report a bug or give feedback")
            val body =
                Uri.encode("Message:\n\n\nApp: Blint\nVersion: ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})")
            val uri = "mailto:$email?subject=$subject&body=$body"
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                type = "text/plain"
                data = Uri.parse(uri)
            }
            if (intent.resolveActivity(requireContext().packageManager) != null) {
                startActivity(intent)
            }
        }

        preferenceOnClick(Keys.SIGN_OUT_PREF) {
            lifecycleScope.launch {
                when (viewModel.logOut()) {
                    is Resource.Error -> UiInterface.showSnackBar(getString(R.string.snack_an_error_occurred))
                    is Resource.Success -> findNavController().navigateAndClearStack(R.id.loginFragment)
                }
            }
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) = Unit

}