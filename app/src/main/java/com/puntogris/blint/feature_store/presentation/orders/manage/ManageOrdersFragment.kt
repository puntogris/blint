package com.puntogris.blint.feature_store.presentation.orders.manage

import androidx.annotation.NonNull
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.puntogris.blint.NavigationDirections
import com.puntogris.blint.R
import com.puntogris.blint.common.presentation.base.BaseFragment
import com.puntogris.blint.common.utils.UiInterface
import com.puntogris.blint.databinding.FragmentManageOrdersBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ManageOrdersFragment :
    BaseFragment<FragmentManageOrdersBinding>(R.layout.fragment_manage_orders) {

    private var mediator: TabLayoutMediator? = null

    override fun initializeViews() {
        UiInterface.registerUi(showFab = true, fabIcon = R.drawable.ic_baseline_add_24) {
            findNavController().navigate(NavigationDirections.actionGlobalNewOrderGraphNav())
        }

        binding.viewPager.adapter = ScreenSlidePagerAdapter(childFragmentManager)
        mediator = TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.tab_orders)
                else -> getString(R.string.tab_records)
            }
        }
        mediator?.attach()
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