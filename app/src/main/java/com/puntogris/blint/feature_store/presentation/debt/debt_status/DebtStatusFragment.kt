package com.puntogris.blint.feature_store.presentation.debt.debt_status

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.puntogris.blint.R
import com.puntogris.blint.common.presentation.base.BaseFragment
import com.puntogris.blint.common.utils.UiInterface
import com.puntogris.blint.common.utils.launchAndRepeatWithViewLifecycle
import com.puntogris.blint.databinding.FragmentDebtStatusBinding
import com.puntogris.blint.feature_store.domain.model.order.Debt
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class DebtStatusFragment : BaseFragment<FragmentDebtStatusBinding>(R.layout.fragment_debt_status) {

    private val viewModel: DebtStatusViewModel by viewModels()
    private val args: DebtStatusFragmentArgs by navArgs()

    override fun initializeViews() {
        binding.fragment = this
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        UiInterface.registerUi() {
            onModifyDebtButtonClicked()
        }

        setupDebtsAdapter()
    }

    private fun setupDebtsAdapter() {
        DebtStatusAdapter { onDebtClicked(it) }.let {
            binding.recyclerView.adapter = it
            subscribeUi(it)
        }
    }

    private fun subscribeUi(adapter: DebtStatusAdapter) {
        launchAndRepeatWithViewLifecycle {
            viewModel.traderDebts.collect {
                adapter.submitData(it)
            }
        }
    }

    private fun onDebtClicked(debt: Debt) {

    }

    private fun onModifyDebtButtonClicked() {
        val action =
            DebtStatusFragmentDirections.actionDebtStatusFragmentToModifyDebtFragment(args.trader)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        binding.recyclerView.adapter = null
        super.onDestroyView()
    }
}