package com.puntogris.blint.feature_store.presentation.debt.trader_debts

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.puntogris.blint.R
import com.puntogris.blint.common.utils.launchAndRepeatWithViewLifecycle
import com.puntogris.blint.common.utils.viewBinding
import com.puntogris.blint.databinding.FragmentShowDebtsBinding
import com.puntogris.blint.feature_store.domain.model.Trader
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShowDebtsFragment : Fragment(R.layout.fragment_show_debts) {

    private val viewModel: ShowDebtsViewModel by viewModels()

    private val binding by viewBinding(FragmentShowDebtsBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
