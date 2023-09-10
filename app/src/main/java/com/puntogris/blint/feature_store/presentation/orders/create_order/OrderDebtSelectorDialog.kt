package com.puntogris.blint.feature_store.presentation.orders.create_order

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.navigation.navGraphViewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.puntogris.blint.R
import com.puntogris.blint.common.utils.UiInterface
import com.puntogris.blint.common.utils.getFloat
import com.puntogris.blint.common.utils.hideKeyboard
import com.puntogris.blint.databinding.DialogOrderDebtSelectorBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrderDebtSelectorDialog : DialogFragment() {

    private lateinit var binding: DialogOrderDebtSelectorBinding
    private val viewModel: NewOrderViewModel by navGraphViewModels(R.id.createOrderGraphNav) { defaultViewModelProviderFactory }

    override fun onCreateDialog(savedViewState: Bundle?): Dialog {
        binding = DialogOrderDebtSelectorBinding.inflate(layoutInflater)
        binding.dialog = this

        return MaterialAlertDialogBuilder(requireContext(), R.style.MaterialAlertDialog_rounded)
            .setView(binding.root)
            .setBackgroundInsetStart(20)
            .setBackgroundInsetEnd(20)
            .create()
    }

    fun onPositiveButtonClicked() {
        hideKeyboard()
        val debt = binding.debtAmount.getFloat()
        if (debt <= 0) {
            UiInterface.showSnackBar(getString(R.string.snack_debt_value_error))
        } else {
            viewModel.updateOrderDebt(debt)
            dismiss()
        }
    }
}
