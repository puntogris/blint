package com.puntogris.blint.ui.supplier

import android.os.Bundle
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentSupplierBinding
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.ui.product.ProductDataFragment
import com.puntogris.blint.ui.product.ProductFragmentDirections
import com.puntogris.blint.ui.product.ProductRecordsFragment

class SupplierFragment : BaseFragment<FragmentSupplierBinding>(R.layout.fragment_supplier) {

    private val args: SupplierFragmentArgs by navArgs()
    private var mediator: TabLayoutMediator? = null

    override fun initializeViews() {
        val pagerAdapter = ScreenSlidePagerAdapter(childFragmentManager)
        binding.viewPager.adapter = pagerAdapter
        mediator = TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when(position){
                0 -> "DATOS"
                else -> "HISTORIAL"
            }
        }
        mediator?.attach()
    }

    fun navigateToEditSupplier(){
        val action = SupplierFragmentDirections.actionSupplierFragmentToEditSupplierFragment(args.supplier)
        findNavController().navigate(action)
    }

    private inner class ScreenSlidePagerAdapter(@NonNull parentFragment: FragmentManager) : FragmentStateAdapter(parentFragment, viewLifecycleOwner.lifecycle) {

        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment =
            (if (position == 0 ) SupplierDataFragment() else SupplierRecordsFragment()).apply {
                arguments = Bundle().apply {
                    putParcelable("supplier_key", args.supplier)
                }
            }
    }

    override fun onDestroyView() {
        mediator?.detach()
        mediator = null
        binding.viewPager.adapter = null
        super.onDestroyView()
    }

}