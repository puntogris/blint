package com.puntogris.blint.feature_store.presentation.trader.create_edit

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
import com.puntogris.blint.common.utils.Constants
import com.puntogris.blint.common.utils.UiInterface
import com.puntogris.blint.common.utils.types.Resource
import com.puntogris.blint.common.utils.types.StringValidator
import com.puntogris.blint.databinding.FragmentEditTraderBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@SuppressLint("Range")
@AndroidEntryPoint
class EditTraderFragment : BaseFragment<FragmentEditTraderBinding>(R.layout.fragment_edit_trader) {

    private val viewModel: EditTraderViewModel by viewModels()
    private lateinit var contactPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var contactPickerLauncher: ActivityResultLauncher<Intent>

    override fun initializeViews() {
        binding.fragment = this
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        UiInterface.registerUi(showFab = true, fabIcon = R.drawable.ic_baseline_save_24) {
            when (val validator = StringValidator.from(
                viewModel.currentTrader.value.name,
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
        setupTraderFilter()
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

    private fun saveClient() {
        lifecycleScope.launch {
            when (viewModel.saveClient()) {
                is Resource.Error ->
                    UiInterface.showSnackBar(getString(R.string.snack_save_trader_error))
                is Resource.Success -> {
                    UiInterface.showSnackBar(getString(R.string.snack_save_trader_success))
                    findNavController().navigateUp()
                }
            }
        }
    }

    private fun setupTraderFilter() {
        //Todo think a better way, the data binding adapter feels weird
        binding.traderInformationLayout.traderType.addOnButtonCheckedListener { _, checkedId, _ ->
            when (checkedId) {
                R.id.traderTypeOtherButton -> viewModel.updateTraderType(Constants.OTHER)
                R.id.traderTypeClientButton -> viewModel.updateTraderType(Constants.CLIENT)
                R.id.traderTypeSupplierButton -> viewModel.updateTraderType(Constants.SUPPLIER)
            }
        }
    }

    fun onAddClientFromContactsClicked() {
        contactPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
    }
}
