package com.puntogris.blint.feature_store.presentation.trader.manage

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.puntogris.blint.R
import com.puntogris.blint.common.presentation.base.BaseFragment
import com.puntogris.blint.common.utils.hideKeyboard
import com.puntogris.blint.common.utils.launchAndRepeatWithViewLifecycle
import com.puntogris.blint.common.utils.registerToolbarBackButton
import com.puntogris.blint.common.utils.showEmptyUiOnEmptyAdapter
import com.puntogris.blint.common.utils.types.TraderFilter
import com.puntogris.blint.databinding.FragmentManageTradersBinding
import com.puntogris.blint.feature_store.domain.model.Trader
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class ManageTradersFragment :
    BaseFragment<FragmentManageTradersBinding>(R.layout.fragment_manage_traders) {

    private val viewModel: ManageTradersViewModel by viewModels()

    override fun initializeViews() {
        binding.viewModel = viewModel

        setupToolbar()
        setupClientsAdapter()
        setupTraderFilter()
    }

    private fun setupClientsAdapter() {
        ManageTradersAdapter { onClientClickListener(it) }.let {
            binding.recyclerView.adapter = it
            subscribeUi(it)
        }
    }

    private fun subscribeUi(adapter: ManageTradersAdapter) {
        launchAndRepeatWithViewLifecycle {
            viewModel.tradersFlow.collect {
                adapter.submitData(it)
            }
        }
        launchAndRepeatWithViewLifecycle {
            showEmptyUiOnEmptyAdapter(adapter, binding.emptyUi)
        }
    }

    private fun onClientClickListener(trader: Trader) {
        hideKeyboard()
        val action = ManageTradersFragmentDirections.actionGlobalTraderFragment(trader.traderId)
        findNavController().navigate(action)
    }

    private fun setupTraderFilter() {
        binding.tradersFilter.addOnButtonCheckedListener { _, checkedId, _ ->
            when (checkedId) {
                R.id.filterAllTradersButton -> viewModel.setFilter(TraderFilter.All)
                R.id.filterClientsTradersButton -> viewModel.setFilter(TraderFilter.CLIENT)
                R.id.filterSuppliersTradersButton -> viewModel.setFilter(TraderFilter.SUPPLIER)
                R.id.filterOtherTradersButton -> viewModel.setFilter(TraderFilter.OTHER)
            }
        }
    }

    private fun setupToolbar(){
        binding.toolbar.apply {
            registerToolbarBackButton(this)
            setOnMenuItemClickListener {
                if (it.itemId == R.id.action_menu_item_add) {
                    findNavController().navigate(R.id.editTraderFragment)
                }
                true
            }
        }
    }

    override fun onDestroyView() {
        binding.recyclerView.adapter = null
        super.onDestroyView()
    }
}