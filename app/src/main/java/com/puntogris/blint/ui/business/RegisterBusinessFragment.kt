package com.puntogris.blint.ui.business

import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentRegisterBusinessBinding
import com.puntogris.blint.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterBusinessFragment : BaseFragment<FragmentRegisterBusinessBinding>(R.layout.fragment_register_business) {

    private var mediator: TabLayoutMediator? = null

    override fun initializeViews() {
        val pagerAdapter = ScreenSlidePagerAdapter(childFragmentManager)
        binding.viewPager.adapter = pagerAdapter
        mediator = TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when(position){
                0 -> getString(R.string.local)
                else -> getString(R.string.online)
            }
        }
        mediator?.attach()
    }

    private inner class ScreenSlidePagerAdapter(@NonNull parentFragment: FragmentManager) : FragmentStateAdapter(
        parentFragment, viewLifecycleOwner.lifecycle
    ) {
        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment =
            if (position == 0) LocalBusinessFragment()
            else OnlineBusinessFragment()

    }

    override fun onDestroyView() {
        mediator?.detach()
        mediator = null
        binding.viewPager.adapter = null
        super.onDestroyView()
    }

    fun registerLocalBusiness(){
        findNavController().navigate(R.id.action_registerBusinessFragment_to_registerLocalBusinessFragment)
    }

    fun registerOnlineBusiness(){
        findNavController().navigate(R.id.action_registerBusinessFragment_to_registerOnlineBusinessFragment)
    }
}