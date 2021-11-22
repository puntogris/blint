package com.puntogris.blint.ui.supplier.create_edit

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.ContentResolver
import android.content.Intent
import android.provider.ContactsContract
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentEditSupplierBinding
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.UiInterface
import com.puntogris.blint.utils.types.SimpleResult
import com.puntogris.blint.utils.types.StringValidator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@SuppressLint("Range")
@AndroidEntryPoint
class EditSupplierFragment :
    BaseFragment<FragmentEditSupplierBinding>(R.layout.fragment_edit_supplier) {

    private val viewModel: EditSupplierViewModel by viewModels()
    lateinit var contactPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var contactPickerLauncher: ActivityResultLauncher<Intent>
    private var contactPickerResultCode = 0

    override fun initializeViews() {
        binding.fragment = this
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        UiInterface.registerUi(showFab = true, fabIcon = R.drawable.ic_baseline_save_24) {
            when (val validator = StringValidator.from(
                viewModel.currentSupplier.value.companyName,
                allowSpecialChars = true,
                isName = true,
                maxLength = 20
            )) {
                is StringValidator.Valid -> {
                    lifecycleScope.launch {
                        when (viewModel.saveSupplier()) {
                            SimpleResult.Failure ->
                                UiInterface.showSnackBar(getString(R.string.snack_save_supplier_error))
                            SimpleResult.Success -> {
                                UiInterface.showSnackBar(getString(R.string.snack_save_supplier_success))
                                findNavController().navigateUp()
                            }
                        }
                    }
                }
                is StringValidator.NotValid -> UiInterface.showSnackBar(getString(validator.error))
            }
        }

        setupContactPickerLauncher()
        setupContactPermissions()
    }

    private fun setupContactPickerLauncher() {
        contactPickerLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    result.data?.let {
                        val contactUri = it.data!!
                        val resolver = requireActivity().contentResolver
                        val cursorLookUpKey = resolver.query(
                            contactUri,
                            arrayOf(ContactsContract.Data.LOOKUP_KEY),
                            null,
                            null,
                            null
                        )

                        cursorLookUpKey?.let { cursor ->
                            if (cursor.moveToFirst()) {
                                val lookUpKey =
                                    cursor.getString(cursor.getColumnIndex(ContactsContract.Data.LOOKUP_KEY))
                                if (lookUpKey != null) {
                                    getEmailWithLookUpKey(lookUpKey, contactPickerResultCode)
                                    getPhoneAndNameWithLookUpKey(lookUpKey, contactPickerResultCode)
                                    if (contactPickerResultCode == 1) loadAddressWithLookUpKey(
                                        lookUpKey
                                    )
                                }
                            }
                        }
                        cursorLookUpKey?.close()
                    }
                }
            }
    }

    private fun setupContactPermissions() {
        contactPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission())
            { isGranted: Boolean ->
                if (isGranted) {
                    val intent = Intent(Intent.ACTION_PICK).apply {
                        type = ContactsContract.Contacts.CONTENT_TYPE
                    }
                    contactPickerLauncher.launch(intent)
                } else UiInterface.showSnackBar(getString(R.string.snack_require_contact_permission))
            }
    }

    fun onAddContactInfoClicked(code: Int) {
        contactPickerResultCode = code
        contactPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
    }

    private fun getEmailWithLookUpKey(key: String, requestCode: Int) {
        val emailWhere =
            ContactsContract.Data.LOOKUP_KEY + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?"
        val emailWhereParams = arrayOf(
            key,
            ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE
        )
        val emailCursor = requireActivity().contentResolver.query(
            ContactsContract.Data.CONTENT_URI,
            null,
            emailWhere,
            emailWhereParams,
            null
        )
        if (emailCursor!!.moveToNext()) {
            val emailId = emailCursor.getString(
                emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA)
            )
            if (requestCode == 1) {
                if (!emailId.isNullOrEmpty()) binding.companyLayout.supplierCompanyEmailText.setText(
                    emailId
                )
            } else {
                if (!emailId.isNullOrEmpty()) binding.sellerLayout.supplierSellerEmailText.setText(
                    emailId
                )
            }
        }
        emailCursor.close()

    }

    private fun loadAddressWithLookUpKey(key: String) {
        val addrWhere =
            ContactsContract.Data.LOOKUP_KEY + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?"
        val addrWhereParams =
            arrayOf(key, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE)
        val addrCursor = requireActivity().contentResolver.query(
            ContactsContract.Data.CONTENT_URI,
            null,
            addrWhere,
            addrWhereParams,
            null
        )
        if (addrCursor!!.moveToNext()) {
            val formattedAddress: String =
                addrCursor.getString(addrCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS))
            if (formattedAddress.isNotEmpty()) binding.companyLayout.supplierCompanyAddressText.setText(
                formattedAddress
            )

        }
        addrCursor.close()
    }

    private fun getPhoneAndNameWithLookUpKey(key: String, requestCode: Int) {
        val contentResolver: ContentResolver = requireActivity().contentResolver
        val contactWhere =
            ContactsContract.Data.LOOKUP_KEY + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?"
        val contactWhereParams =
            arrayOf(key, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
        val cursorPhone = contentResolver.query(
            ContactsContract.Data.CONTENT_URI,
            null,
            contactWhere,
            contactWhereParams,
            null
        )
        if (cursorPhone != null && cursorPhone.count > 0) {
            if (cursorPhone.moveToNext()) {
                if (cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))
                        .toInt() > 0
                ) {
                    val givenName =
                        cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                    val phoneNo =
                        cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))


                    if (requestCode == 1) {
                        if (!givenName.isNullOrEmpty()) binding.companyLayout.supplierCompanyNameText.setText(
                            givenName
                        )
                        if (!phoneNo.isNullOrEmpty()) binding.companyLayout.supplierCompanyPhoneText.setText(
                            phoneNo
                        )
                    } else {
                        if (!givenName.isNullOrEmpty()) binding.sellerLayout.supplierSellerNameText.setText(
                            givenName
                        )
                        if (!phoneNo.isNullOrEmpty()) binding.sellerLayout.supplierSellerPhoneText.setText(
                            phoneNo
                        )
                    }
                }
            }
        }

        cursorPhone?.close()
    }
}