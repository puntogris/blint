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
    }

    private fun onSaveDebtClicked(){
        lifecycleScope.launch {
            if(binding.debtTypeText.getString() == "-"){
                args.debt.amount = -binding.debtAmountText.getFloat()
            }else{
                args.debt.amount = binding.debtAmountText.getFloat()
            }

            when(viewModel.registerNewDebt(args.debt)){
                SimpleResult.Failure -> {
                    UiInterface.showSnackBar(getString(R.string.snack_update_debt_error))
                }
                SimpleResult.Success -> {
                    UiInterface.showSnackBar(getString(R.string.snack_update_debt_success))
                    findNavController().navigateUp()
                }
            }
        }
    }

}