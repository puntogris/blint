package com.puntogris.blint.ui.about

import android.content.Intent
import android.net.Uri
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
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
import com.puntogris.blint.utils.showShortSnackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AboutFragment : BaseFragment<FragmentAboutBinding>(R.layout.fragment_about) {

    private val viewModel:PreferencesViewModel by viewModels()

    override fun initializeViews() {
        binding.fragment = this
    }

    fun onSendSuggestionClicked(){
        InputSheet().show(requireParentFragment().requireContext()) {
            title(getString(R.string.problems_and_advice))
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
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(APP_PLAY_STORE_URI)
            setPackage(PLAY_STORE_PACKAGE)
        }
        startActivity(intent)
    }
    fun onPrivacyPolicyClicked(){
        val action = AboutFragmentDirections.actionAboutFragmentToWebPageFragment(PRIVACY_POLICY_URI)
        findNavController().navigate(action)

    }
    fun onTermsAndConditionsClicked(){
        val action = AboutFragmentDirections.actionAboutFragmentToWebPageFragment(TERMS_AND_CONDITIONS_URI)
        findNavController().navigate(action)
    }

    private fun onResultSendReport(message: String){
        lifecycleScope.launch {
            when(viewModel.sendReport(message)){
                SimpleResult.Success -> showShortSnackBar(getString(R.string.snack_send_suggestion_report_success))
                SimpleResult.Failure -> showShortSnackBar(getString(R.string.snack_error_connection_server_try_later))
            }
        }
    }
}