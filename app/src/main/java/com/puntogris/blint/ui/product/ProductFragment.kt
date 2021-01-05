package com.puntogris.blint.ui.product

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentProductBinding
import com.puntogris.blint.ui.base.BaseFragment

class ProductFragment : BaseFragment<FragmentProductBinding>(R.layout.fragment_product) {

    override fun initializeViews() {
        // The pager adapter, which provides the pages to the view pager widget.
        val pagerAdapter = ScreenSlidePagerAdapter(requireActivity())
        binding.viewPager.adapter = pagerAdapter
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when(position){
                0 -> "DATA"
                else -> "HISTORIAL"
            }
        }.attach()
    }

    private inner class ScreenSlidePagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment {
            return if (position == 0) ProductDataFragment()
            else ProductHistoryFragment()
        }
    }

    override fun onDestroyView() {
        binding.viewPager.adapter = null
        super.onDestroyView()
    }
}