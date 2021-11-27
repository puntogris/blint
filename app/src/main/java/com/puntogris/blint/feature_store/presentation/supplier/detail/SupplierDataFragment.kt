package com.puntogris.blint.feature_store.presentation.supplier.detail

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract.Contacts
import android.provider.ContactsContract.Intents
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
import com.puntogris.blint.databinding.FragmentSupplierDataBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SupplierDataFragment :
    BaseFragment<FragmentSupplierDataBinding>(R.layout.fragment_supplier_data) {

    private val viewModel: SupplierViewModel by viewModels()
    private lateinit var contactPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var contactPickerLauncher: ActivityResultLauncher<Intent>
    private var permissionCode = 0

    override fun initializeViews() {
        binding.fragment = this
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        setupContactPickerLauncher()
        setupContactPermissionLauncher()
    }

    private fun setupContactPermissionLauncher() {
        contactPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission())
            { isGranted: Boolean ->
                if (isGranted) {
                    Intent(Intent.ACTION_INSERT).apply {
                        type = Contacts.CONTENT_TYPE
                        with(viewModel.currentSupplier.value) {
                            if (permissionCode == 1) {
                                putExtra(Intents.Insert.NAME, companyName)
                                putExtra(Intents.Insert.PHONE, companyPhone)
                                putExtra(Intents.Insert.EMAIL, companyEmail)
                            } else {
                                putExtra(Intents.Insert.NAME, sellerName)
                                putExtra(Intents.Insert.PHONE, sellerPhone)
                                putExtra(Intents.Insert.EMAIL, sellerEmail)
                            }
                        }
                    }.also { contactPickerLauncher.launch(it) }
                } else UiInterface.showSnackBar(getString(R.string.snack_require_contact_permission))
            }
    }

    private fun setupContactPickerLauncher() {
        contactPickerLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                UiInterface.showSnackBar(
                    getString(
                        if (it.resultCode == Activity.RESULT_OK) R.string.snack_action_success
                        else R.string.snack_an_error_occurred
                    )
                )
            }
    }

    fun onCreateContactClicked(code: Int) {
        permissionCode = code
        contactPermissionLauncher.launch(android.Manifest.permission.WRITE_CONTACTS)
    }

    fun onEmailButtonClicked(code: Int) {
        val email = if (code == 0) {
            viewModel.currentSupplier.value.companyEmail
        } else {
            viewModel.currentSupplier.value.sellerEmail
        }
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:$email")
        }
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(intent)
        }
    }

    fun onPhoneButtonClicked(code: Int) {
        OptionsSheet().show(requireParentFragment().requireContext()) {
            displayMode(DisplayMode.LIST)
            with(
                Option(R.drawable.ic_baseline_call_24, R.string.action_call),
                Option(R.drawable.ic_baseline_message_24, R.string.action_message),
                Option(R.drawable.ic_whatsapp, R.string.action_whats_app)
            )
            onPositive { index: Int, _: Option ->
                val phone = if (code == 0) {
                    viewModel.currentSupplier.value.companyPhone
                } else {
                    viewModel.currentSupplier.value.sellerPhone
                }
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

}