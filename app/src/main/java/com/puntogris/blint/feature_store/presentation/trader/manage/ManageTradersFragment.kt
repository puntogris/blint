package com.puntogris.blint.feature_store.presentation.trader.manage

import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.puntogris.blint.R
import com.puntogris.blint.common.utils.hideKeyboard
import com.puntogris.blint.common.utils.launchAndRepeatWithViewLifecycle
import com.puntogris.blint.common.utils.registerToolbarBackButton
import com.puntogris.blint.common.utils.showEmptyUiOnEmptyAdapter
import com.puntogris.blint.common.utils.types.TraderFilter
import com.puntogris.blint.common.utils.viewBinding
import com.puntogris.blint.databinding.FragmentManageTradersBinding
import com.puntogris.blint.feature_store.domain.model.Trader
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ManageTradersFragment : Fragment(R.layout.fragment_manage_traders) {

    private val viewModel: ManageTradersViewModel by viewModels()

    private val binding by viewBinding(FragmentManageTradersBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupClientsAdapter()
        setupTraderFilter()
        setupViews()
    }

    private fun setupViews() {
        binding.editTextTradersSearch.doAfterTextChanged { editable ->
            if (editable != null) {
                viewModel.setQuery(editable)
            }
        }
    }

    private fun setupClientsAdapter() {
        val adapter = ManageTradersAdapter { onClientClickListener(it) }
        binding.recyclerViewTraders.adapter = adapter
        subscribeUi(adapter)
    }

    private fun subscribeUi(adapter: ManageTradersAdapter) {
        launchAndRepeatWithViewLifecycle {
            viewModel.tradersFlow.collect {
                adapter.submitData(it)
            }
        }
        launchAndRepeatWithViewLifecycle {
            showEmptyUiOnEmptyAdapter(adapter, binding.manageTradersEmptyUi.root)
        }
    }

    private fun onClientClickListener(trader: Trader) {
        hideKeyboard()
        val action = ManageTradersFragmentDirections.actionGlobalTraderFragment(trader.traderId)
        findNavController().navigate(action)
    }

    private fun setupTraderFilter() {
        binding.buttonGroupTradersFilters.addOnButtonCheckedListener { _, checkedId, _ ->
            when (checkedId) {
                R.id.button_traders_filters_all -> viewModel.setFilter(TraderFilter.All)
                R.id.button_traders_filters_clients -> viewModel.setFilter(TraderFilter.CLIENT)
                R.id.button_traders_filters_suppliers -> viewModel.setFilter(TraderFilter.SUPPLIER)
                R.id.button_traders_filters_other -> viewModel.setFilter(TraderFilter.OTHER)
            }
        }
    }

    private fun setupToolbar() {
        with(binding.toolbar) {
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
        binding.recyclerViewTraders.adapter = null
        super.onDestroyView()
    }
}