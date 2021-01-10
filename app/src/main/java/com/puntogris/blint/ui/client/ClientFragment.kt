package com.puntogris.blint.ui.client

import android.os.Bundle
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.transition.MaterialFadeThrough
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentClientBinding
import com.puntogris.blint.databinding.FragmentRegisterBusinessBinding
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.ui.product.ProductDataFragment
import com.puntogris.blint.ui.supplier.SupplierDataFragment
import com.puntogris.blint.ui.supplier.SupplierFragmentArgs
import com.puntogris.blint.ui.supplier.SupplierHistoryFragment
import com.puntogris.blint.utils.changeIconFromDrawable
import com.puntogris.blint.utils.getParentFab
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ClientFragment : BaseFragment<FragmentClientBinding>(R.layout.fragment_client) {

    private val args: ClientFragmentArgs by navArgs()
    private var mediator: TabLayoutMediator? = null

    override fun initializeViews() {
        val adapterSize = if (args.client == null) 1 else 2
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
                if (args.client == null) ClientDataFragment()
                else ClientDataFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable("client_key", args.client)
                    }
                }
            } else ClientHistoryFragment()
    }

    override fun onDestroyView() {
        mediator?.detach()
        mediator = null
        binding.viewPager.adapter = null
        super.onDestroyView()
    }
}