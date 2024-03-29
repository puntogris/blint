package com.puntogris.blint.feature_store.presentation.orders.manage

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.puntogris.blint.R
import com.puntogris.blint.common.utils.showOrderPickerAndNavigate
import com.puntogris.blint.common.utils.viewBinding
import com.puntogris.blint.databinding.FragmentManageOrdersBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ManageOrdersFragment : Fragment(R.layout.fragment_manage_orders) {

    private var mediator: TabLayoutMediator? = null

    private val binding by viewBinding(FragmentManageOrdersBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerToolbar()
        setupViewPager()
    }

    private fun setupViewPager() {
        binding.manageOrdersViewPager.adapter = ScreenSlidePagerAdapter(childFragmentManager)
        mediator = TabLayoutMediator(
            binding.tabLayoutManageOrders,
            binding.manageOrdersViewPager
        ) { tab, position ->
            tab.setText(if (position == 0) R.string.tab_orders else R.string.tab_records)
        }
        mediator?.attach()
    }

    private fun registerToolbar() {
        binding.toolbar.setOnMenuItemClickListener {
            if (it.itemId == R.id.action_menu_item_add) {
                showOrderPickerAndNavigate()
            }
            true
        }
    }

    private inner class ScreenSlidePagerAdapter(parentFragment: FragmentManager) :
        FragmentStateAdapter(parentFragment, viewLifecycleOwner.lifecycle) {

        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment {
            return if (position == 0) OrdersTabFragment() else RecordsTabFragment()
        }
    }

    override fun onDestroyView() {
        mediator?.detach()
        mediator = null
        binding.manageOrdersViewPager.adapter = null
        super.onDestroyView()
    }
}
