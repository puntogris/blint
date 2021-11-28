package com.puntogris.blint.feature_store.presentation.debt.trader_debts

import androidx.fragment.app.viewModels
import com.puntogris.blint.R
import com.puntogris.blint.common.presentation.base.BaseFragment
import com.puntogris.blint.common.utils.UiInterface
import com.puntogris.blint.common.utils.launchAndRepeatWithViewLifecycle
import com.puntogris.blint.databinding.FragmentShowDebtsBinding
import com.puntogris.blint.feature_store.domain.model.SimpleDebt
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class ShowDebtsFragment : BaseFragment<FragmentShowDebtsBinding>(R.layout.fragment_show_debts) {

    private val viewModel: ShowDebtsViewModel by viewModels()

    override fun initializeViews() {
        UiInterface.registerUi()
        setupDebtsAdapter()
    }

    private fun setupDebtsAdapter() {
        SimpleDebtAdapter { onDebtClicked(it) }.let {
            binding.recyclerView.adapter = it
            subscribeUi(it)
        }
    }

    private fun subscribeUi(adapter: SimpleDebtAdapter) {
        launchAndRepeatWithViewLifecycle {
            viewModel.debtsFlow.collect {
                adapter.submitData(it)
            }
        }
    }

    private fun onDebtClicked(simpleDebt: SimpleDebt) {

    }
}