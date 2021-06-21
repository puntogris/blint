package com.puntogris.blint.ui.debt

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.collection.arrayMapOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentModifyDebtBinding
import com.puntogris.blint.model.Debt
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ModifyDebtFragment : BaseFragment<FragmentModifyDebtBinding>(R.layout.fragment_modify_debt) {

    private val viewModel: DebtViewModel by viewModels()
    private val args: ModifyDebtFragmentArgs by navArgs()

    override fun initializeViews() {
        getParentFab().apply {
            changeIconFromDrawable(R.drawable.ic_baseline_save_24)
            setOnClickListener { onSaveDebtClicked() }
        }
        binding.debtTypeText.setAdapter(ArrayAdapter(requireContext(),R.layout.dropdown_item_list, listOf("+", "-")))
    }

    private fun onSaveDebtClicked(){

        lifecycleScope.launch {
            args.debt.amount = if(binding.debtTypeText.getString() == "+"){
                binding.debtAmountText.getFloat()
            }else {
                -binding.debtAmountText.getFloat()
            }
            viewModel.registerNewDebt(args.debt, args.debtType)
            showLongSnackBarAboveFab("Se actualizo la deuda correctamente.")
            findNavController().navigateUp()
        }

    }

}