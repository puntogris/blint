package com.puntogris.blint.ui.debt

import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentModifyDebtBinding
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.*
import com.puntogris.blint.utils.Constants.UPDATE_DEBT_KEY
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ModifyDebtFragment : BaseFragment<FragmentModifyDebtBinding>(R.layout.fragment_modify_debt) {

    private val viewModel: DebtViewModel by viewModels()
    private val args: ModifyDebtFragmentArgs by navArgs()

    override fun initializeViews() {
        UiInterface.register(showFab = true, fabIcon = R.drawable.ic_baseline_save_24){
            onSaveDebtClicked()
        }
        binding.debtTypeText.setAdapter(ArrayAdapter(requireContext(),R.layout.dropdown_item_list, listOf("+", "-")))
        binding.debtTypeText.setOnFocusChangeListener { _, _ -> hideKeyboard() }
    }

    private fun onSaveDebtClicked(){
        lifecycleScope.launch {
            args.debt.amount =
                if (binding.debtTypeText.getString() == "-") -binding.debtAmountText.getFloat()
                else binding.debtAmountText.getFloat()

            when(viewModel.registerNewDebt(args.debt)){
                SimpleResult.Failure -> {
                    UiInterface.showSnackBar(getString(R.string.snack_update_debt_error))
                }
                SimpleResult.Success -> {
                    UiInterface.showSnackBar(getString(R.string.snack_update_debt_success))
                    findNavController().apply {
                        previousBackStackEntry!!.savedStateHandle.set(UPDATE_DEBT_KEY, args.debt.amount)
                        popBackStack()
                    }
                }
            }
        }
    }

}