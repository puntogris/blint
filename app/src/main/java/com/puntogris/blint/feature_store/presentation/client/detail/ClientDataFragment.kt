package com.puntogris.blint.feature_store.presentation.client.detail

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import com.maxkeppeler.sheets.options.DisplayMode
import com.maxkeppeler.sheets.options.Option
import com.maxkeppeler.sheets.options.OptionsSheet
import com.puntogris.blint.R
import com.puntogris.blint.common.presentation.base.BaseFragment
import com.puntogris.blint.common.utils.Constants.WHATS_APP_PACKAGE
import com.puntogris.blint.common.utils.UiInterface
import com.puntogris.blint.databinding.FragmentClientDataBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ClientDataFragment : BaseFragment<FragmentClientDataBinding>(R.layout.fragment_client_data) {

    private val viewModel: ClientViewModel by viewModels(ownerProducer = {requireParentFragment()})
    lateinit var contactPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var contactPickerLauncher: ActivityResultLauncher<Intent>

    override fun initializeViews() {
        binding.let {
            it.fragment = this
            it.viewModel = viewModel
            it.lifecycleOwner = viewLifecycleOwner
        }

        setupContactPickerLauncher()
        setupContactPermissions()
    }

    private fun setupContactPickerLauncher() {
        contactPickerLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                val resultMessage =
                    if (it.resultCode == Activity.RESULT_OK) R.string.snack_action_success
                    else R.string.snack_an_error_occurred
                UiInterface.showSnackBar(getString(resultMessage))
            }
    }

    private fun setupContactPermissions() {
        contactPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission())
            { isGranted: Boolean ->
                if (isGranted) {
                    val intent = Intent(Intent.ACTION_INSERT).apply {
                        type = ContactsContract.Contacts.CONTENT_TYPE
                        putExtra(
                            ContactsContract.Intents.Insert.NAME,
                            viewModel.currentClient.value.name
                        )
                        putExtra(
                            ContactsContract.Intents.Insert.PHONE,
                            viewModel.currentClient.value.phone
                        )
                        putExtra(
                            ContactsContract.Intents.Insert.EMAIL,
                            viewModel.currentClient.value.email
                        )
                        putExtra(
                            ContactsContract.Intents.Insert.POSTAL,
                            viewModel.currentClient.value.address
                        )
                    }
                    contactPickerLauncher.launch(intent)
                } else UiInterface.showSnackBar(getString(R.string.snack_require_contact_permission))
            }
    }

    fun onAddContactButtonClicked() {
        contactPermissionLauncher.launch(android.Manifest.permission.WRITE_CONTACTS)
    }

    fun onPhoneButtonClicked() {
        OptionsSheet().show(requireParentFragment().requireContext()) {
            displayMode(DisplayMode.LIST)
            with(
                Option(R.drawable.ic_baseline_call_24, R.string.action_call),
                Option(R.drawable.ic_baseline_message_24, R.string.action_message),
                Option(R.drawable.ic_whatsapp, R.string.action_whats_app)
            )
            onPositive { index: Int, _: Option ->
                val phone = viewModel.currentClient.value.phone
                if (index == 0) {
                    Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null))
                } else {
                    Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:$phone"))
                }.also {
                    if (index == 2) it.setPackage(WHATS_APP_PACKAGE)
                    contactPickerLauncher.launch(it)
                }
            }
        }
    }

    fun onEmailButtonClicked() {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:${viewModel.currentClient.value.email}")
        }
        contactPickerLauncher.launch(intent)
    }
}