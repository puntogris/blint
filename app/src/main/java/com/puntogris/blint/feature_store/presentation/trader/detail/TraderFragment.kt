package com.puntogris.blint.feature_store.presentation.trader.detail

import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.maxkeppeler.sheets.info.InfoSheet
import com.puntogris.blint.R
import com.puntogris.blint.common.presentation.base.BaseFragment
import com.puntogris.blint.common.utils.UiInterface
import com.puntogris.blint.common.utils.registerToolbarBackButton
import com.puntogris.blint.common.utils.types.Resource
import com.puntogris.blint.databinding.FragmentTraderBinding
import com.puntogris.blint.feature_store.domain.model.order.Record
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TraderFragment : BaseFragment<FragmentTraderBinding>(R.layout.fragment_trader) {

    private val args: TraderFragmentArgs by navArgs()
    private var mediator: TabLayoutMediator? = null
    private val viewModel: TraderViewModel by viewModels()

    override fun initializeViews() {
        registerToolbar()
        setupPager()
    }

    private fun registerToolbar() {
        binding.toolbar.apply {
            registerToolbarBackButton(this)

            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.action_edit_trader -> {
                        navigateToEditClientFragment()
                    }
                    R.id.action_delete_trader -> {
                        navigateToDeleteTrader()
                    }
                    R.id.action_update_trader_debt -> {
                        navigateToUpdateDebt()
                    }
                }
                true
            }
        }
    }

    private fun setupPager() {
        binding.viewPager.adapter = ScreenSlidePagerAdapter(childFragmentManager)
        mediator = TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            val res = when (position) {
                0 -> R.string.tab_data
                1 -> R.string.tab_records
                else -> R.string.tab_debts
            }
            tab.setText(res)
        }
        mediator?.attach()
    }

    private fun navigateToEditClientFragment() {
        val action = TraderFragmentDirections.actionTraderFragmentToEditTraderFragment(
            viewModel.currentTrader.value
        )
        findNavController().navigate(action)
    }

    private inner class ScreenSlidePagerAdapter(@NonNull parentFragment: FragmentManager) :
        FragmentStateAdapter(parentFragment, viewLifecycleOwner.lifecycle) {

        override fun getItemCount(): Int = 3

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> TraderDataFragment()
                1 -> TraderRecordsFragment()
                else -> TraderDebtFragment()
            }.apply {
                arguments = args.toBundle()
            }
        }
    }

    private fun onDeleteTraderConfirmed() {
        lifecycleScope.launch {
            when (viewModel.deleteTrader()) {
                is Resource.Error ->
                    UiInterface.showSnackBar(getString(R.string.snack_delete_trader_error))
                is Resource.Success -> {
                    UiInterface.showSnackBar(getString(R.string.snack_delete_trader_success))
                    findNavController().navigateUp()
                }
            }
        }
    }

    fun navigateToUpdateDebt() {
        val action = TraderFragmentDirections.actionTraderFragmentToUpdateDebtDialog(
            viewModel.currentTrader.value
        )
        findNavController().navigate(action)
    }

    fun navigateToInfoRecord(record: Record) {
        val action = TraderFragmentDirections.actionGlobalOrderFragment(orderId = record.orderId)
        findNavController().navigate(action)
    }

    private fun navigateToDeleteTrader() {
        InfoSheet().show(requireParentFragment().requireContext()) {
            title(R.string.ask_delete_trader_title)
            content(R.string.delete_client_warning)
            onNegative(R.string.action_cancel)
            onPositive(R.string.action_yes) { onDeleteTraderConfirmed() }
        }
    }

    override fun onDestroyView() {
        mediator?.detach()
        mediator = null
        binding.viewPager.adapter = null
        super.onDestroyView()
    }
}