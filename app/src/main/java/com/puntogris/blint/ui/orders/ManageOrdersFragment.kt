package com.puntogris.blint.ui.orders

import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.maxkeppeler.sheets.core.SheetStyle
import com.maxkeppeler.sheets.options.DisplayMode
import com.maxkeppeler.sheets.options.Option
import com.maxkeppeler.sheets.options.OptionsSheet
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentManageOrdersBinding
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.getParentFab
import com.puntogris.blint.utils.setUpUi
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ManageOrdersFragment : BaseFragment<FragmentManageOrdersBinding>(R.layout.fragment_manage_orders) {

    private var mediator: TabLayoutMediator? = null

    override fun initializeViews() {
        setUpUi(showFab = true, showToolbar = false, fabIcon = R.drawable.ic_baseline_add_24){
            onCreateNewOrderClicked()
        }
        binding.searchToolbar.setNavigationOnClickListener { findNavController().navigateUp() }

        binding.viewPager.adapter = ScreenSlidePagerAdapter(childFragmentManager)
        mediator = TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when(position){
                0 -> "ORDENES"
                else -> "MOVIMIENTOS"
            }
        }
        mediator?.attach()
    }

    private fun onCreateNewOrderClicked(){
        OptionsSheet().build(requireContext()){
            displayMode(DisplayMode.GRID_HORIZONTAL)
            style(SheetStyle.DIALOG)
            with(
                Option(R.drawable.ic_baseline_speed_24,"Crear orden rapida."),
                Option(R.drawable.ic_baseline_timer_24,"Crear orden detallada."),
            )
            onPositive { index: Int, _ ->
                if (index == 0){

                }else{
                    onCreateDetailedOrderClicked()
                }
            }
        }.show(parentFragmentManager,"")
    }

    fun onCreateDetailedOrderClicked(){
        findNavController().navigate(R.id.newOrderGraphNav)
    }

    private inner class ScreenSlidePagerAdapter(@NonNull parentFragment: FragmentManager) : FragmentStateAdapter(parentFragment, viewLifecycleOwner.lifecycle) {

        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment =
            (if (position == 0 ) OrdersTabFragment()
            else RecordsTabFragment())
    }

    override fun onDestroyView() {
        mediator?.detach()
        mediator = null
        binding.viewPager.adapter = null
        super.onDestroyView()
    }
}