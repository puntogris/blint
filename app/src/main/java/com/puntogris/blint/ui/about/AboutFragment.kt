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
import com.puntogris.blint.utils.SimpleResult
import com.puntogris.blint.utils.showShortSnackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AboutFragment : BaseFragment<FragmentAboutBinding>(R.layout.fragment_about) {

    private val viewModel:PreferencesViewModel by viewModels()

    override fun initializeViews() {
        binding.termsConditions.setOnClickListener {
            findNavController().navigate(R.id.action_aboutFragment_to_termsConditionsFragment)
        }
        binding.privacyPolicy.setOnClickListener {
            findNavController().navigate(R.id.action_aboutFragment_to_privacyPolicyFragment)
        }

        binding.rateAppButton.setOnClickListener {
               val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(APP_PLAY_STORE_URI)
                setPackage(PLAY_STORE_PACKAGE)
            }
            startActivity(intent)
        }
        binding.sendSuggestion.setOnClickListener {
            InputSheet().show(requireParentFragment().requireContext()) {
                title("Problemas y consejos")
                //content("Reporta un problema o envianos tus consejos.")
                with(InputEditText {
                    required()
                    label("Que nos queres contar?")
                    hint("Mensaje.")
                    onPositive("Enviar") {
                        onResultSendReport(value.toString())
                    }
                    onNegative("Cancelar")
                })
            }
        }
    }

    private fun onResultSendReport(message: String){
        lifecycleScope.launch {
            when(viewModel.sendReport(message)){
                SimpleResult.Success -> showShortSnackBar("Se envio satisfactioriamente el reporte, Gracias por tu ayuda!")
                SimpleResult.Failure -> showShortSnackBar("Ocurrio un error. Verifique su conexion ntente nuevamente.")
            }
        }
    }
}