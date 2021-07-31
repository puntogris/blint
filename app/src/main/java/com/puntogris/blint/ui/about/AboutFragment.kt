package com.puntogris.blint.ui.about

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.maxkeppeler.sheets.input.InputSheet
import com.maxkeppeler.sheets.input.type.InputEditText
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentAboutBinding
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.ui.settings.PreferencesViewModel
import com.puntogris.blint.utils.Constants.APP_PLAY_STORE_URI
import com.puntogris.blint.utils.Constants.PLAY_STORE_PACKAGE
import com.puntogris.blint.utils.Constants.PRIVACY_POLICY_URI
import com.puntogris.blint.utils.Constants.TERMS_AND_CONDITIONS_URI
import com.puntogris.blint.utils.SimpleResult
import com.puntogris.blint.utils.launchWebBrowserIntent
import com.puntogris.blint.utils.UiInterface
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AboutFragment : BaseFragment<FragmentAboutBinding>(R.layout.fragment_about) {

    private val viewModel:PreferencesViewModel by viewModels()

    override fun initializeViews() {
        UiInterface.registerUi(showAppBar = false)
        binding.fragment = this
    }

    fun onSendSuggestionClicked(){
        InputSheet().show(requireParentFragment().requireContext()) {
            title(this@AboutFragment.getString(R.string.problems_and_advice))
            //content("Reporta un problema o envianos tus consejos.")
            with(InputEditText {
                required()
                label(this@AboutFragment.getString(R.string.tell_us_more))
                hint(this@AboutFragment.getString(R.string.message_hint))
                onPositive(this@AboutFragment.getString(R.string.action_send)) {
                    onResultSendReport(value.toString())
                }
                onNegative(this@AboutFragment.getString(R.string.action_cancel))
            })
        }
    }

    fun onRateAppClicked(){
        launchWebBrowserIntent(APP_PLAY_STORE_URI, PLAY_STORE_PACKAGE)
    }

    fun onPrivacyPolicyClicked(){
        launchWebBrowserIntent(PRIVACY_POLICY_URI)
    }

    fun onTermsAndConditionsClicked(){
        launchWebBrowserIntent(TERMS_AND_CONDITIONS_URI)
    }

    private fun onResultSendReport(message: String){
        lifecycleScope.launch {
            val result = when(viewModel.sendReport(message)){
                SimpleResult.Success -> R.string.snack_send_suggestion_report_success
                SimpleResult.Failure -> R.string.snack_error_connection_server_try_later
            }
            UiInterface.showSnackBar(getString(result))
        }
    }
}