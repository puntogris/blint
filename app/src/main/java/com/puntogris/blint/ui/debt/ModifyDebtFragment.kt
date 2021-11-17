package com.puntogris.blint.ui.debt

import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentModifyDebtBinding
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.Constants.UPDATE_DEBT_KEY
import com.puntogris.blint.utils.UiInterface
import com.puntogris.blint.utils.getFloat
import com.puntogris.blint.utils.getString
import com.puntogris.blint.utils.hideKeyboard
import com.puntogris.blint.utils.types.SimpleResult
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ModifyDebtFragment : BaseFragment<FragmentModifyDebtBinding>(R.layout.fragment_modify_debt) {

    private val viewModel: DebtViewModel by viewModels()
    private val args: ModifyDebtFragmentArgs by navArgs()

    override fun initializeViews() {
        UiInterface.registerUi(showFab = true, fabIcon = R.drawable.ic_baseline_save_24) {
            if (binding.debtAmountText.getString().toFloatOrNull() != null &&
                binding.debtAmountText.getFloat() > 0
            ) {
                onSaveDebtClicked()
            } else UiInterface.showSnackBar(getString(R.string.snack_amount_cant_be_empty))
        }
        binding.debtTypeText.apply {
            setAdapter(
                ArrayAdapter(
                    requireContext(),
                    R.layout.dropdown_item_list,
                    listOf("+", "-")
                )
            )
            setOnFocusChangeListener { _, _ -> hideKeyboard() }
        }
    }

    private fun onSaveDebtClicked() {
        lifecycleScope.launch {
            args.debt.amount =
                if (binding.debtTypeText.getString() == "-") -binding.debtAmountText.getFloat()
                else binding.debtAmountText.getFloat()

            when (viewModel.registerNewDebt(args.debt)) {
                SimpleResult.Failure -> UiInterface.showSnackBar(getString(R.string.snack_update_debt_error))
                SimpleResult.Success -> {
                    UiInterface.showSnackBar(getString(R.string.snack_update_debt_success))
                    findNavController().apply {
                        previousBackStackEntry!!.savedStateHandle.set(
                            UPDATE_DEBT_KEY,
                            args.debt.amount
                        )
                        popBackStack()
                    }
                }
            }
        }
    }

}