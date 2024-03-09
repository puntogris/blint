package com.puntogris.blint.feature_store.presentation.trader.detail

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.paging.LoadState
import com.puntogris.blint.R
import com.puntogris.blint.common.utils.launchAndRepeatWithViewLifecycle
import com.puntogris.blint.common.utils.setDebtColor
import com.puntogris.blint.common.utils.showEmptyUiOnEmptyAdapter
import com.puntogris.blint.common.utils.viewBinding
import com.puntogris.blint.databinding.FragmentGenerateReportBinding
import com.puntogris.blint.databinding.FragmentTraderDebtBinding
import com.puntogris.blint.feature_store.domain.model.order.Debt
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.map

@AndroidEntryPoint
class TraderDebtFragment : Fragment(R.layout.fragment_trader_debt) {

    private val viewModel: TraderViewModel by viewModels(ownerProducer = { requireParentFragment() })

    private val binding by viewBinding(FragmentTraderDebtBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner

        setupDebtsAdapter()
        setupObservers()
    }

    private fun setupObservers() {
        launchAndRepeatWithViewLifecycle {
            viewModel.currentTrader.collectLatest {
                binding.textViewDebtBalance.setDebtColor(it.debt)
            }
        }
    }

    private fun setupDebtsAdapter() {
        val adapter = DebtStatusAdapter { onDebtClicked(it) }
        binding.recyclerViewDebts.adapter = adapter
        subscribeUi(adapter)
    }

    private fun subscribeUi(adapter: DebtStatusAdapter) {
        launchAndRepeatWithViewLifecycle {
            viewModel.traderDebts.collect {
                adapter.submitData(it)
            }
        }
        launchAndRepeatWithViewLifecycle {
            adapter.loadStateFlow
                .distinctUntilChangedBy { it.source.refresh }
                .map { it.source.refresh is LoadState.NotLoading }
                .collect { notLoading ->
                    if (notLoading) binding.recyclerViewDebts.scrollToPosition(0)
                }
        }
        launchAndRepeatWithViewLifecycle {
            showEmptyUiOnEmptyAdapter(adapter, binding.traderDebtEmptyUi)
        }
    }

    private fun onDebtClicked(debt: Debt) {
        // TODO add a bottom sheet with the debt info and a button to navigate to the order
    }

    override fun onDestroyView() {
        binding.recyclerViewDebts.adapter = null
        super.onDestroyView()
    }
}
