package com.puntogris.blint.feature_store.presentation.client.create_edit

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.provider.ContactsContract
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
import com.puntogris.blint.databinding.FragmentEditClientBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@SuppressLint("Range")
@AndroidEntryPoint
class EditClientFragment : BaseFragment<FragmentEditClientBinding>(R.layout.fragment_edit_client) {

    private val viewModel: EditClientViewModel by viewModels()
    private lateinit var contactPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var contactPickerLauncher: ActivityResultLauncher<Intent>

    override fun initializeViews() {
        binding.fragment = this
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        UiInterface.registerUi(showFab = true, fabIcon = R.drawable.ic_baseline_save_24) {
            when (val validator = StringValidator.from(
                viewModel.currentClient.value.name,
                allowSpecialChars = true,
                isName = true,
                maxLength = 20
            )) {
                is StringValidator.Valid -> saveClient()
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
                    viewModel.setContact(it)
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

    private fun saveClient(){
        lifecycleScope.launch {
            when (viewModel.saveClient()) {
                SimpleResult.Failure ->
                    UiInterface.showSnackBar(getString(R.string.snack_save_client_error))
                SimpleResult.Success -> {
                    UiInterface.showSnackBar(getString(R.string.snack_save_client_success))
                    findNavController().navigateUp()
                }
            }
        }
    }

    fun onAddClientFromContactsClicked() {
        contactPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
    }
}
