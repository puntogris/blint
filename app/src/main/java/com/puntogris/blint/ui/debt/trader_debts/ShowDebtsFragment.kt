package com.puntogris.blint.ui.debt.trader_debts

import androidx.fragment.app.viewModels
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentShowDebtsBinding
import com.puntogris.blint.model.SimpleDebt
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.ui.debt.manage.ManageDebtsViewModel
import com.puntogris.blint.utils.UiInterface
import com.puntogris.blint.utils.launchAndRepeatWithViewLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

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
            viewModel.getDebtsFlow().collect {
                adapter.submitData(it)
            }
        }
    }

    private fun onDebtClicked(simpleDebt: SimpleDebt) {

    }
}