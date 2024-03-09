package com.puntogris.blint.feature_store.presentation.debt.trader_debts

import androidx.fragment.app.viewModels
import com.puntogris.blint.R
import com.puntogris.blint.common.presentation.base.BaseFragment
import com.puntogris.blint.common.utils.launchAndRepeatWithViewLifecycle
import com.puntogris.blint.databinding.FragmentShowDebtsBinding
import com.puntogris.blint.feature_store.domain.model.Trader
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShowDebtsFragment : BaseFragment<FragmentShowDebtsBinding>(R.layout.fragment_show_debts) {

    private val viewModel: ShowDebtsViewModel by viewModels()

    override fun initializeViews() {
        setupDebtsAdapter()
    }

    private fun setupDebtsAdapter() {
        val adapter = TraderDebtAdapter { onDebtClicked(it) }
        binding.showDebtsRecyclerView.adapter = adapter
        subscribeUi(adapter)
    }

    private fun subscribeUi(adapter: TraderDebtAdapter) {
        launchAndRepeatWithViewLifecycle {
            viewModel.debtsFlow.collect {
                adapter.submitData(it)
            }
        }
    }

    private fun onDebtClicked(trader: Trader) {
        // TODO implement onDebtClicked flow
    }

    override fun onDestroyView() {
        binding.showDebtsRecyclerView.adapter = null
        super.onDestroyView()
    }
}
