package com.puntogris.blint.feature_store.presentation.debt.manage

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.puntogris.blint.R
import com.puntogris.blint.common.utils.launchAndRepeatWithViewLifecycle
import com.puntogris.blint.common.utils.registerToolbarBackButton
import com.puntogris.blint.common.utils.setDebtColorWithLimit
import com.puntogris.blint.common.utils.showEmptyUiOnEmptyAdapter
import com.puntogris.blint.common.utils.viewBinding
import com.puntogris.blint.databinding.FragmentManageDebtBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ManageDebtFragment : Fragment(R.layout.fragment_manage_debt) {

    private val viewModel: ManageDebtsViewModel by viewModels()

    private val binding by viewBinding(FragmentManageDebtBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerToolbarBackButton(binding.toolbar)
        setupDebtsAdapter()
        setupObservers()
    }

    private fun setupObservers() {
        launchAndRepeatWithViewLifecycle {
            viewModel.currentBusiness.collectLatest {
                binding.textViewStoreDebt.setDebtColorWithLimit(it.tradersDebt)

            }
        }
    }

    private fun setupDebtsAdapter() {
        val adapter = ManageDebtsAdapter()
        binding.recyclerViewDebts.adapter = adapter
        subscribeUi(adapter)
    }

    private fun subscribeUi(adapter: ManageDebtsAdapter) {
        launchAndRepeatWithViewLifecycle {
            viewModel.debtsFlow.collect {
                adapter.submitData(it)
            }
        }
        launchAndRepeatWithViewLifecycle {
            showEmptyUiOnEmptyAdapter(adapter, binding.manageDebtEmptyUi)
        }
    }

    override fun onDestroyView() {
        binding.recyclerViewDebts.adapter = null
        super.onDestroyView()
    }
}
