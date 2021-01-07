package com.puntogris.blint.ui.business

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentRegisterBusinessBinding
import com.puntogris.blint.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterBusinessFragment : BaseFragment<FragmentRegisterBusinessBinding>(R.layout.fragment_register_business) {

    override fun initializeViews() {

        val pagerAdapter = ScreenSlidePagerAdapter(this)
        binding.viewPager.adapter = pagerAdapter
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when(position){
                0 -> "LOCAL"
                else -> "ONLINE"
            }
        }.attach()
    }

    private inner class ScreenSlidePagerAdapter(parentFragment: Fragment) : FragmentStateAdapter(parentFragment) {

        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment =
            if (position == 0) LocalBusinessFragment()
             else OnlineBusinessFragment()
    }

    override fun onDestroyView() {
        binding.viewPager.adapter = null
        super.onDestroyView()
    }

    fun registerLocalBusiness(){
        RegisterBusinessFragmentDirections.
    }

    fun registerOnlineBusiness(){
        RegisterBusinessFragmentDirections.actionRegisterBusinessFragmentToRegisterOnlineBusinessFragment()
    }
}