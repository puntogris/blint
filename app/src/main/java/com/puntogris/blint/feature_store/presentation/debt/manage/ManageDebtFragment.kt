package com.puntogris.blint.feature_store.presentation.debt.manage

import androidx.fragment.app.viewModels
import com.puntogris.blint.R
import com.puntogris.blint.common.presentation.base.BaseFragment
import com.puntogris.blint.common.utils.UiInterface
import com.puntogris.blint.common.utils.launchAndRepeatWithViewLifecycle
import com.puntogris.blint.common.utils.showEmptyUiOnEmptyAdapter
import com.puntogris.blint.databinding.FragmentManageDebtBinding
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

    private fun setupDebtsAdapter() {
        ManageDebtsAdapter().let {
            binding.recyclerView.adapter = it
            subscribeUi(it)
        }
    }

    private fun subscribeUi(adapter: ManageDebtsAdapter) {
        launchAndRepeatWithViewLifecycle {
            viewModel.debtsFlow.collect {
                adapter.submitData(it)
            }
        }
        launchAndRepeatWithViewLifecycle {
            showEmptyUiOnEmptyAdapter(adapter, binding.emptyUi)
        }
    }

}