package com.puntogris.blint.feature_store.presentation.trader.detail

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.maxkeppeler.sheets.options.DisplayMode
import com.maxkeppeler.sheets.options.Option
import com.maxkeppeler.sheets.options.OptionsSheet
import com.puntogris.blint.R
import com.puntogris.blint.common.utils.Constants.WHATS_APP_PACKAGE
import com.puntogris.blint.common.utils.UiInterface
import com.puntogris.blint.common.utils.launchAndRepeatWithViewLifecycle
import com.puntogris.blint.common.utils.setCapitalizeWord
import com.puntogris.blint.common.utils.setTextOrDefault
import com.puntogris.blint.common.utils.setTraderType
import com.puntogris.blint.common.utils.viewBinding
import com.puntogris.blint.databinding.FragmentTraderDataBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class TraderDataFragment : Fragment(R.layout.fragment_trader_data) {

    private val viewModel: TraderViewModel by viewModels(ownerProducer = { requireParentFragment() })
    private lateinit var contactPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var contactPickerLauncher: ActivityResultLauncher<Intent>

    private val binding by viewBinding(FragmentTraderDataBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        setupListeners()
        setupContactPickerLauncher()
        setupContactPermissions()
    }

    private fun setupObservers() {
        launchAndRepeatWithViewLifecycle {
            viewModel.currentTrader.collectLatest {
                setCapitalizeWord(binding.textViewTraderName, it.name)
                setTextOrDefault(binding.textViewTraderPhone, it.phone)
                setTextOrDefault(binding.textViewTraderEmail, it.email)
                setTextOrDefault(binding.textViewTraderAddress, it.address)
                setTraderType(binding.textViewTraderType, it.type)
                setTextOrDefault(binding.textViewTraderBilling, it.billing)
                setTextOrDefault(binding.textViewTraderNotes, it.notes)
            }
        }
    }

    private fun setupListeners() {
        binding.imageViewTraderContactIcon.setOnClickListener {
            onAddContactButtonClicked()
        }
        binding.imageViewTraderPhoneIcon.setOnClickListener {
            onPhoneButtonClicked()
        }
        binding.imageViewTraderEmailIcon.setOnClickListener {
            onEmailButtonClicked()
        }
    }

    private fun setupContactPickerLauncher() {
        contactPickerLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                val resultMessage = if (it.resultCode == Activity.RESULT_OK) {
                    R.string.snack_action_success
                } else {
                    R.string.snack_an_error_occurred
                }
                UiInterface.showSnackBar(getString(resultMessage))
            }
    }

    private fun setupContactPermissions() {
        contactPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    contactPickerLauncher.launch(viewModel.getContactIntent())
                } else {
                    UiInterface.showSnackBar(getString(R.string.snack_require_contact_permission))
                }
            }
    }

    fun onAddContactButtonClicked() {
        contactPermissionLauncher.launch(android.Manifest.permission.WRITE_CONTACTS)
    }

    fun onPhoneButtonClicked() {
        OptionsSheet().build(requireContext()) {
            displayMode(DisplayMode.LIST)
            with(
                Option(R.drawable.ic_baseline_call_24, R.string.action_call),
                Option(R.drawable.ic_baseline_message_24, R.string.action_message),
                Option(R.drawable.ic_whatsapp, R.string.action_whats_app)
            )
            onPositive { index: Int, _: Option ->
                val phone = viewModel.currentTrader.value.phone
                val intent = if (index == 0) {
                    Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null))
                } else {
                    Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:$phone"))
                }
                if (index == 2) {
                    intent.setPackage(WHATS_APP_PACKAGE)
                }
                contactPickerLauncher.launch(intent)
            }
        }.show(parentFragmentManager, "")
    }

    fun onEmailButtonClicked() {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:${viewModel.currentTrader.value.email}")
        }
        contactPickerLauncher.launch(intent)
    }
}
