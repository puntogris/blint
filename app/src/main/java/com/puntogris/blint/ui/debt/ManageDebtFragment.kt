package com.puntogris.blint.ui.debt

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.maxkeppeler.sheets.options.Option
import com.maxkeppeler.sheets.options.OptionsSheet
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentManageDebtBinding
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.Constants.CLIENT_DEBT
import com.puntogris.blint.utils.Constants.SUPPLIER_DEBT
import com.puntogris.blint.utils.getParentFab
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class ManageDebtFragment : BaseFragment<FragmentManageDebtBinding>(R.layout.fragment_manage_debt) {

    private val viewModel: DebtViewModel by viewModels()

    override fun initializeViews() {
        binding.fragment = this
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        val debtPagingAdapter = DebtPagingAdapter(){}

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = debtPagingAdapter
        }

        lifecycleScope.launchWhenStarted {
            viewModel.getAllDebts().collect {
                debtPagingAdapter.submitData(it)
            }
        }


    }

    fun showClientsDebts(){
        val action = ManageDebtFragmentDirections.actionManageDebtFragmentToShowDebtsFragment(CLIENT_DEBT)
        findNavController().navigate(action)
    }

    fun showSuppliersDebts(){
        val action = ManageDebtFragmentDirections.actionManageDebtFragmentToShowDebtsFragment(SUPPLIER_DEBT)
        findNavController().navigate(action)
    }

}