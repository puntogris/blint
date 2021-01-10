package com.puntogris.blint.ui.supplier

import android.os.Bundle
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentSupplierBinding
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.ui.product.ProductDataFragment
import com.puntogris.blint.ui.product.ProductFragmentArgs
import com.puntogris.blint.ui.product.ProductHistoryFragment
import com.puntogris.blint.utils.changeIconFromDrawable
import com.puntogris.blint.utils.getParentFab

class SupplierFragment : BaseFragment<FragmentSupplierBinding>(R.layout.fragment_supplier) {

    private val args: SupplierFragmentArgs by navArgs()
    private var mediator: TabLayoutMediator? = null

    override fun initializeViews() {
        val adapterSize = if (args.supplier == null) 1 else 2
        val pagerAdapter = ScreenSlidePagerAdapter(childFragmentManager, adapterSize)
        binding.viewPager.adapter = pagerAdapter
        mediator = TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when(position){
                0 -> "DATOS"
                else -> "HISTORIAL"
            }
        }
        mediator?.attach()
    }

    private inner class ScreenSlidePagerAdapter(@NonNull parentFragment: FragmentManager, val adapterSize: Int) : FragmentStateAdapter(parentFragment, viewLifecycleOwner.lifecycle) {

        override fun getItemCount(): Int = adapterSize

        override fun createFragment(position: Int): Fragment =
            if (position == 0) {
                if (args.supplier == null) SupplierDataFragment()
                else SupplierDataFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable("supplier_key", args.supplier)
                    }
                }
            } else SupplierHistoryFragment()
    }

    override fun onDestroyView() {
        mediator?.detach()
        mediator = null
        binding.viewPager.adapter = null
        super.onDestroyView()
    }
}