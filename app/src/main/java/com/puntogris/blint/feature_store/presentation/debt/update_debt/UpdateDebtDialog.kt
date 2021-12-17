package com.puntogris.blint.feature_store.presentation.debt.update_debt

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.puntogris.blint.R
import com.puntogris.blint.common.utils.UiInterface
import com.puntogris.blint.common.utils.getFloat
import com.puntogris.blint.common.utils.hideKeyboard
import com.puntogris.blint.common.utils.types.Resource
import com.puntogris.blint.databinding.DialogUpdateDebtBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UpdateDebtDialog : DialogFragment() {

    private val viewModel: UpdateDebtViewModel by viewModels()
    private lateinit var binding: DialogUpdateDebtBinding

    override fun onCreateDialog(savedViewState: Bundle?): Dialog {
        binding = DialogUpdateDebtBinding.inflate(layoutInflater)
        binding.dialog = this

        return MaterialAlertDialogBuilder(requireContext())
            .setView(binding.root)
            .setBackgroundInsetStart(20)
            .setBackgroundInsetEnd(20)
            .create()
    }

    fun onSaveDebtClicked() {
        val amount = binding.debtAmount.getFloat()

        lifecycleScope.launch {
            when (viewModel.saveDebt(amount)) {
                is Resource.Error -> {
                    UiInterface.showSnackBar(getString(R.string.snack_update_debt_error))
                }
                is Resource.Success -> {
                    UiInterface.showSnackBar(getString(R.string.snack_update_debt_success))
                    findNavController().navigateUp()
                }
            }
            dismiss()
        }
    }

    override fun dismiss() {
        hideKeyboard()
        super.dismiss()
    }
}