package com.puntogris.blint.ui.client

import android.os.Bundle
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentClientBinding
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.ui.product.ProductDataFragment
import com.puntogris.blint.ui.product.ProductFragmentDirections
import com.puntogris.blint.ui.product.ProductRecordsFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ClientFragment : BaseFragment<FragmentClientBinding>(R.layout.fragment_client) {

    private val args: ClientFragmentArgs by navArgs()
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

    fun navigateToEditClient(){
        val action = ClientFragmentDirections.actionClientFragmentToEditClientFragment(args.client)
        findNavController().navigate(action)
    }

    private inner class ScreenSlidePagerAdapter(@NonNull parentFragment: FragmentManager) : FragmentStateAdapter(parentFragment, viewLifecycleOwner.lifecycle) {

        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment =
            (if (position == 0 ) ClientDataFragment() else ClientRecordsFragment()).apply {
                arguments = Bundle().apply {
                    putParcelable("client_key", args.client)
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