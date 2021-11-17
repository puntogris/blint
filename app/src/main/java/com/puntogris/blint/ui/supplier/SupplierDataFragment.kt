package com.puntogris.blint.ui.supplier

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
import com.puntogris.blint.databinding.FragmentSupplierDataBinding
import com.puntogris.blint.model.Supplier
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.Constants.SUPPLIER_DATA_KEY
import com.puntogris.blint.utils.Constants.WHATS_APP_PACKAGE
import com.puntogris.blint.utils.UiInterface
import com.puntogris.blint.utils.takeArgsIfNotNull
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SupplierDataFragment :
    BaseFragment<FragmentSupplierDataBinding>(R.layout.fragment_supplier_data) {

    private val viewModel: SupplierViewModel by viewModels()
    lateinit var requestPermissionContact: ActivityResultLauncher<String>
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private var permissionCode = 0

    override fun initializeViews() {
        binding.fragment = this
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        takeArgsIfNotNull<Supplier>(SUPPLIER_DATA_KEY) {
            viewModel.setSupplierData(it)
        }

        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {}

        requestPermissionContact =
            registerForActivityResult(ActivityResultContracts.RequestPermission())
            { isGranted: Boolean ->
                if (isGranted) {
                    Intent(Intent.ACTION_INSERT).apply {
                        type = ContactsContract.Contacts.CONTENT_TYPE
                        if (permissionCode == 1) {
                            putExtra(
                                ContactsContract.Intents.Insert.NAME,
                                viewModel.currentSupplier.value!!.companyName
                            )
                            putExtra(
                                ContactsContract.Intents.Insert.PHONE,
                                viewModel.currentSupplier.value!!.companyPhone
                            )
                            putExtra(
                                ContactsContract.Intents.Insert.EMAIL,
                                viewModel.currentSupplier.value!!.companyEmail
                            )
                        } else {
                            putExtra(
                                ContactsContract.Intents.Insert.NAME,
                                viewModel.currentSupplier.value!!.sellerName
                            )
                            putExtra(
                                ContactsContract.Intents.Insert.PHONE,
                                viewModel.currentSupplier.value!!.sellerPhone
                            )
                            putExtra(
                                ContactsContract.Intents.Insert.EMAIL,
                                viewModel.currentSupplier.value!!.sellerEmail
                            )
                        }
                    }.also { activityResultLauncher.launch(it) }
                } else UiInterface.showSnackBar(getString(R.string.snack_require_contact_permission))
            }
    }

    fun onCreateContactClicked(code: Int) {
        permissionCode = code
        requestPermissionContact.launch(android.Manifest.permission.WRITE_CONTACTS)
    }

    fun onEmailButtonClicked(code: Int) {
        val email =
            if (code == 0) viewModel.currentSupplier.value!!.companyEmail
            else viewModel.currentSupplier.value!!.sellerEmail
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:$email")
        }
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(intent)
        }
    }

    fun onPhoneButtonClicked(code: Int) {
        val phone =
            if (code == 0) viewModel.currentSupplier.value!!.companyPhone
            else viewModel.currentSupplier.value!!.sellerPhone
        OptionsSheet().build(requireContext()) {
            displayMode(DisplayMode.LIST)
            with(
                Option(
                    R.drawable.ic_baseline_call_24,
                    this@SupplierDataFragment.getString(R.string.action_call)
                ),
                Option(
                    R.drawable.ic_baseline_message_24,
                    this@SupplierDataFragment.getString(R.string.action_message)
                ),
                Option(
                    R.drawable.ic_whatsapp,
                    this@SupplierDataFragment.getString(R.string.action_whats_app)
                )
            )
            onPositive { index: Int, _: Option ->
                when (index) {
                    0 -> {
                        val uri = Uri.fromParts("tel", phone, null)
                        val intent = Intent(Intent.ACTION_DIAL, uri)
                        startActivity(intent)
                    }
                    1 -> {
                        val uri = Uri.parse("smsto:$phone")
                        val intent = Intent(Intent.ACTION_SENDTO, uri)
                        startActivity(intent)
                    }
                    2 -> {
                        val uri = Uri.parse("smsto:$phone")
                        val intent = Intent(Intent.ACTION_SENDTO, uri)
                        intent.setPackage(WHATS_APP_PACKAGE)
                        startActivity(intent)
                    }
                }
            }
        }.show(parentFragmentManager, "")
    }

}