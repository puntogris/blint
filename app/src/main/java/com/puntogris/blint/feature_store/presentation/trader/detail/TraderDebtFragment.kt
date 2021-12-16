package com.puntogris.blint.feature_store.presentation.trader.detail

import androidx.fragment.app.viewModels
import com.puntogris.blint.R
import com.puntogris.blint.common.presentation.base.BaseFragment
import com.puntogris.blint.common.utils.UiInterface
import com.puntogris.blint.common.utils.launchAndRepeatWithViewLifecycle
import com.puntogris.blint.databinding.FragmentTraderDebtBinding
import com.puntogris.blint.feature_store.domain.model.order.Debt
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class TraderDebtFragment : BaseFragment<FragmentTraderDebtBinding>(R.layout.fragment_trader_debt) {

    private val viewModel: TraderViewModel by viewModels(ownerProducer = { requireParentFragment() })

    override fun initializeViews() {
        binding.fragment = this
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        UiInterface.registerUi()
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
        //TODO add a bottom sheet with the debt info and a button to navigate to the order
    }

    override fun onDestroyView() {
        binding.recyclerView.adapter = null
        super.onDestroyView()
    }
}