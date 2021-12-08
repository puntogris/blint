package com.puntogris.blint.feature_store.presentation.supplier.create_edit

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.provider.ContactsContract.Contacts
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.puntogris.blint.R
import com.puntogris.blint.common.presentation.base.BaseFragment
import com.puntogris.blint.common.utils.UiInterface
import com.puntogris.blint.common.utils.types.SimpleResult
import com.puntogris.blint.common.utils.types.StringValidator
import com.puntogris.blint.databinding.FragmentEditSupplierBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@SuppressLint("Range")
@AndroidEntryPoint
class EditSupplierFragment :
    BaseFragment<FragmentEditSupplierBinding>(R.layout.fragment_edit_supplier) {

    private val viewModel: EditSupplierViewModel by viewModels()
    private lateinit var contactPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var contactPickerLauncher: ActivityResultLauncher<Intent>
    private var contactPickerCode = 0

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
                is StringValidator.Valid -> saveSupplier()
                is StringValidator.NotValid -> UiInterface.showSnackBar(getString(validator.error))
            }
        }

        setupContactPickerLauncher()
        setupContactPermissions()
    }

    private fun setupContactPickerLauncher() {
        contactPickerLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                result.takeIf { it.resultCode == RESULT_OK }?.data?.data?.let {
                    viewModel.setContact(it, contactPickerCode)
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

    private fun saveSupplier() {
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

    fun onAddContactInfoClicked(code: Int) {
        contactPickerCode = code
        contactPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
    }
}