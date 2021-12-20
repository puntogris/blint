package com.puntogris.blint.feature_store.presentation.orders.manage

import androidx.annotation.NonNull
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.puntogris.blint.R
import com.puntogris.blint.common.presentation.base.BaseFragment
import com.puntogris.blint.common.utils.showOrderPickerAndNavigate
import com.puntogris.blint.databinding.FragmentManageOrdersBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ManageOrdersFragment :
    BaseFragment<FragmentManageOrdersBinding>(R.layout.fragment_manage_orders) {

    private var mediator: TabLayoutMediator? = null

    override fun initializeViews() {
        registerToolbar()
        setupViewPager()
    }

    private fun setupViewPager() {
        binding.manageOrdersViewPager.adapter = ScreenSlidePagerAdapter(childFragmentManager)
        mediator = TabLayoutMediator(
            binding.manageOrdersTabLayout,
            binding.manageOrdersViewPager
        ) { tab, position ->
            tab.setText(if (position == 0) R.string.tab_orders else R.string.tab_records)
        }
        mediator?.attach()
    }

    private fun registerToolbar() {
        binding.manageOrdersToolbar.setOnMenuItemClickListener {
            if (it.itemId == R.id.action_menu_item_add) {
                showOrderPickerAndNavigate()
            }
            true
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
        binding.manageOrdersViewPager.adapter = null
        super.onDestroyView()
    }
}