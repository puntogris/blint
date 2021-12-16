package com.puntogris.blint.feature_store.presentation.debt.update_debt

import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.puntogris.blint.R
import com.puntogris.blint.common.presentation.base.BaseFragment
import com.puntogris.blint.common.utils.UiInterface
import com.puntogris.blint.common.utils.getFloat
import com.puntogris.blint.common.utils.getString
import com.puntogris.blint.common.utils.hideKeyboard
import com.puntogris.blint.databinding.FragmentModifyDebtBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UpdateDebtFragment : BaseFragment<FragmentModifyDebtBinding>(R.layout.fragment_modify_debt) {

    private val viewModel: UpdateDebtViewModel by viewModels()

    override fun initializeViews() {
        UiInterface.registerUi(fabIcon = R.drawable.ic_baseline_save_24) {
            if (
                binding.debtAmount.getString().toFloatOrNull() != null &&
                binding.debtAmount.getFloat() > 0
            ) {
                onSaveDebtClicked()
            } else {
                UiInterface.showSnackBar(getString(R.string.snack_amount_cant_be_empty))
            }
        }
        setupAmountSignAdapter()
    }

    private fun setupAmountSignAdapter() {
        binding.debtTypeSelector.apply {
            setAdapter(
                ArrayAdapter(
                    requireContext(),
                    R.layout.dropdown_item_list,
                    resources.getStringArray(R.array.debt_signs)
                )
            )
            setOnFocusChangeListener { _, _ -> hideKeyboard() }
        }
    }

    private fun onSaveDebtClicked() {
        lifecycleScope.launch {
            val sign = binding.debtTypeSelector.getString()
            val amount = binding.debtAmount.getFloat()

//            val result = when (viewModel.saveDebt(amountSign = sign, debtAmount = amount)) {
//                is Resource.Error -> R.string.snack_update_debt_error
//                is Resource.Success -> {
//                    findNavController().navigateUp()
//                    R.string.snack_update_debt_success
//                }
//            }
         //   UiInterface.showSnackBar(getString(result))
        }
    }
}