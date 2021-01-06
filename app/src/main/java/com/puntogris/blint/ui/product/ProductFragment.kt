package com.puntogris.blint.ui.product

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentProductBinding
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.gone
import com.puntogris.blint.utils.invisible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductFragment : BaseFragment<FragmentProductBinding>(R.layout.fragment_product) {

    private val args: ProductFragmentArgs by navArgs()

    override fun initializeViews() {
        val adapterSize = if (args.product == null) 1 else 2
        val pagerAdapter = ScreenSlidePagerAdapter(this, adapterSize)
        binding.viewPager.adapter = pagerAdapter
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when(position){
                0 -> "DATA"
                else -> "HISTORIAL"
            }
        }.attach()
    }

    private inner class ScreenSlidePagerAdapter(parentFragment: Fragment, val adapterSize: Int) : FragmentStateAdapter(parentFragment) {

        override fun getItemCount(): Int = adapterSize
        
        override fun createFragment(position: Int): Fragment  =
            if (position == 0) {
                if (args.product == null) ProductDataFragment()
                else ProductDataFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable("data_key", args.product)
                    }
                }
            } else ProductHistoryFragment()
        }


    fun goToScannerFragment(){
        findNavController().navigate(ProductFragmentDirections.actionProductFragmentToScannerFragment())
    }

    override fun onDestroyView() {
        binding.viewPager.adapter = null
        super.onDestroyView()
    }
}