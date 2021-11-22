package com.puntogris.blint.ui.supplier.create_edit

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.provider.ContactsContract.*
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
    private lateinit var contactPermissionLauncher: ActivityResultLauncher<String>
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
                if (result.resultCode == RESULT_OK && result.data?.data != null) {

                    requireActivity().contentResolver.query(
                        result.data?.data!!,
                        arrayOf(Data.LOOKUP_KEY),
                        null,
                        null,
                        null
                    )?.let {
                        if (it.moveToFirst()) {
                            val lookUpKey = it.getString(it.getColumnIndex(Data.LOOKUP_KEY))
                            if (lookUpKey != null) {
                                getEmailWithLookUpKey(lookUpKey, contactPickerResultCode)
                                getPhoneAndNameWithLookUpKey(lookUpKey, contactPickerResultCode)
                                if (contactPickerResultCode == 1) loadAddressWithLookUpKey(lookUpKey)
                            }
                        }
                        it.close()
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
                        type = Contacts.CONTENT_TYPE
                    }
                    contactPickerLauncher.launch(intent)
                } else UiInterface.showSnackBar(getString(R.string.snack_require_contact_permission))
            }
    }

    private fun getEmailWithLookUpKey(key: String, requestCode: Int) {
        val emailWhere = Data.LOOKUP_KEY + " = ? AND " + Data.MIMETYPE + " = ?"
        val emailWhereParams = arrayOf(key, CommonDataKinds.Email.CONTENT_ITEM_TYPE)
        requireActivity().contentResolver.query(
            Data.CONTENT_URI,
            null,
            emailWhere,
            emailWhereParams,
            null
        )?.let {
            if (it.moveToNext()) {

                val emailId = it.getString(it.getColumnIndex(CommonDataKinds.Email.DATA))

                if (requestCode == 1) {
                    binding.companyLayout.supplierCompanyEmailText.setText(emailId)
                } else {
                    binding.sellerLayout.supplierSellerEmailText.setText(emailId)
                }
            }
            it.close()
        }
    }

    private fun loadAddressWithLookUpKey(key: String) {
        val addressWhere = Data.LOOKUP_KEY + " = ? AND " + Data.MIMETYPE + " = ?"
        val addressWhereParams = arrayOf(key, CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE)

        requireActivity().contentResolver.query(
            Data.CONTENT_URI,
            null,
            addressWhere,
            addressWhereParams,
            null
        )?.also {
            if (it.moveToNext()) {
                val address = it.getString(
                    it.getColumnIndex(CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS)
                )
                binding.companyLayout.supplierCompanyAddressText.setText(address)
            }
            it.close()
        }
    }

    private fun getPhoneAndNameWithLookUpKey(key: String, requestCode: Int) {
        val contactWhere = Data.LOOKUP_KEY + " = ? AND " + Data.MIMETYPE + " = ?"
        val contactWhereParams = arrayOf(key, CommonDataKinds.Phone.CONTENT_ITEM_TYPE)

        requireActivity().contentResolver.query(
            Data.CONTENT_URI,
            null,
            contactWhere,
            contactWhereParams,
            null
        )?.let {

            if (it.count > 0 && it.moveToNext()) {

                if (it.getString(it.getColumnIndex(Contacts.HAS_PHONE_NUMBER)).toInt() > 0) {
                    val name = it.getString(it.getColumnIndex(Contacts.DISPLAY_NAME))
                    val phone = it.getString(it.getColumnIndex(CommonDataKinds.Phone.NUMBER))

                    if (requestCode == 1) {
                        binding.companyLayout.supplierCompanyName.setText(name)
                        binding.companyLayout.supplierCompanyPhone.setText(phone)
                    } else {
                        binding.sellerLayout.supplierSellerName.setText(name)
                        binding.sellerLayout.supplierSellerPhone.setText(phone)
                    }
                }
            }
            it.close()
        }
    }

    fun onAddContactInfoClicked(code: Int) {
        contactPickerResultCode = code
        contactPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
    }
}