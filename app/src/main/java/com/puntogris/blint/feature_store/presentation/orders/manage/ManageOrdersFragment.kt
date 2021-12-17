package com.puntogris.blint.feature_store.presentation.orders.manage

import androidx.annotation.NonNull
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.maxkeppeler.sheets.options.DisplayMode
import com.maxkeppeler.sheets.options.Option
import com.maxkeppeler.sheets.options.OptionsSheet
import com.puntogris.blint.R
import com.puntogris.blint.common.presentation.base.BaseFragment
import com.puntogris.blint.common.utils.Constants
import com.puntogris.blint.common.utils.UiInterface
import com.puntogris.blint.databinding.FragmentManageOrdersBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ManageOrdersFragment :
    BaseFragment<FragmentManageOrdersBinding>(R.layout.fragment_manage_orders) {

    private var mediator: TabLayoutMediator? = null

    override fun initializeViews() {
        UiInterface.registerUi(showToolbar = false)
        registerToolbar()
        binding.viewPager.adapter = ScreenSlidePagerAdapter(childFragmentManager)
        mediator = TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.setText(if (position == 0) R.string.tab_orders else R.string.tab_records)
        }
        mediator?.attach()

    }

    private fun registerToolbar() {
        binding.toolbar.setOnMenuItemClickListener {
            if (it.itemId == R.id.action_add) {
                showOrderPickerDialog()
            }
            true
        }
    }

    private fun showOrderPickerDialog() {
        OptionsSheet().show(requireParentFragment().requireContext()) {
            displayMode(DisplayMode.LIST)
            with(
                Option(R.drawable.ic_fi_rr_download, R.string.in_entry),
                Option(R.drawable.ic_fi_rr_upload, R.string.out_entry)
            )
            onPositive { index: Int, _ ->
                val type = if (index == 0) Constants.IN else Constants.OUT
                val action =
                    ManageOrdersFragmentDirections.actionGlobalNewOrderGraphNav(orderType = type)
                findNavController().navigate(action)
            }
        }
    }

    private inner class ScreenSlidePagerAdapter(@NonNull parentFragment: FragmentManager) :
        FragmentStateAdapter(parentFragment, viewLifecycleOwner.lifecycle) {

        override fun getItemCount(): Int = 2
        override fun createFragment(position: Int) =
            if (position == 0) OrdersTabFragment() else RecordsTabFragment()
    }

    override fun onDestroyView() {
        mediator?.detach()
        mediator = null
        binding.viewPager.adapter = null
        super.onDestroyView()
    }
}