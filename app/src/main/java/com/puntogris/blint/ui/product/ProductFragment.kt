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
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductFragment : BaseFragment<FragmentProductBinding>(R.layout.fragment_product) {

    private val args: ProductFragmentArgs by navArgs()

    override fun initializeViews() {
        val pagerAdapter = ScreenSlidePagerAdapter(this)
        binding.viewPager.adapter = pagerAdapter
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when(position){
                0 -> "DATA"
                else -> "HISTORIAL"
            }
        }.attach()
    }


    private inner class ScreenSlidePagerAdapter(parentFragment: Fragment) : FragmentStateAdapter(parentFragment) {
        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment {
            return if (position == 0) {
                ProductDataFragment()
                if (args.product == null){
                    ProductDataFragment()
                }else{
                    val fragment = ProductDataFragment()
                    fragment.arguments = Bundle().apply {
                        putParcelable("data_key", args.product)
                    }
                    fragment
                }
            }
            else ProductHistoryFragment()
        }
    }

    fun openFragmentD(){
        findNavController().navigate(ProductFragmentDirections.actionProductFragmentToScannerFragment())
    }


    override fun onDestroyView() {
        binding.viewPager.adapter = null
        super.onDestroyView()
    }
}