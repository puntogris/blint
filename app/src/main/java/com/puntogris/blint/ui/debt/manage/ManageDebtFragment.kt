package com.puntogris.blint.ui.debt.manage

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentManageDebtBinding
import com.puntogris.blint.model.Debt
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.Constants.CLIENT_DEBT
import com.puntogris.blint.utils.Constants.SUPPLIER_DEBT
import com.puntogris.blint.utils.UiInterface
import com.puntogris.blint.utils.launchAndRepeatWithViewLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class ManageDebtFragment : BaseFragment<FragmentManageDebtBinding>(R.layout.fragment_manage_debt) {

    private val viewModel: ManageDebtsViewModel by viewModels()

    override fun initializeViews() {
        binding.fragment = this
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        UiInterface.registerUi()
        setupDebtsAdapter()
    }

    private fun setupDebtsAdapter(){
        ManageDebtsAdapter { onDebtClicked(it) }.let {
            binding.recyclerView.adapter = it
            subscribeUi(it)
        }
    }

    private fun subscribeUi(adapter: ManageDebtsAdapter){
        launchAndRepeatWithViewLifecycle {
            viewModel.debtsFlow.collect {
                adapter.submitData(it)
            }
        }
    }

    private fun onDebtClicked(debt: Debt){

    }

    fun showClientsDebts() {
        val action =
            ManageDebtFragmentDirections.actionManageDebtFragmentToShowDebtsFragment(CLIENT_DEBT)
        findNavController().navigate(action)
    }

    fun showSuppliersDebts() {
        val action =
            ManageDebtFragmentDirections.actionManageDebtFragmentToShowDebtsFragment(SUPPLIER_DEBT)
        findNavController().navigate(action)
    }
}