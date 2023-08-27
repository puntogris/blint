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
            binding.manageTradersRecyclerView.adapter = it
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
            showEmptyUiOnEmptyAdapter(adapter, binding.manageTradersEmptyUi)
        }
    }

    private fun onClientClickListener(trader: Trader) {
        hideKeyboard()
        val action = ManageTradersFragmentDirections.actionGlobalTraderFragment(trader.traderId)
        findNavController().navigate(action)
    }

    private fun setupTraderFilter() {
        binding.manageTradersFilter.addOnButtonCheckedListener { _, checkedId, _ ->
            when (checkedId) {
                R.id.manage_traders_filter_all_button -> viewModel.setFilter(TraderFilter.All)
                R.id.manage_traders_filter_clients_button -> viewModel.setFilter(TraderFilter.CLIENT)
                R.id.manage_traders_filter_suppliers_button -> viewModel.setFilter(TraderFilter.SUPPLIER)
                R.id.manage_traders_filter_other_button -> viewModel.setFilter(TraderFilter.OTHER)
            }
        }
    }

    private fun setupToolbar(){
        binding.manageTradersToolbar.apply {
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
        binding.manageTradersRecyclerView.adapter = null
        super.onDestroyView()
    }
}