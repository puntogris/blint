package com.puntogris.blint.ui.business

import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentOnlineBusinessBinding
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.registerUiInterface

class OnlineBusinessFragment : BaseFragment<FragmentOnlineBusinessBinding>(R.layout.fragment_online_business) {

    override fun initializeViews() {
        registerUiInterface.register(showAppBar = false)
//        binding.onlineBusinessFragment = this
//        binding.animationView.setPadding(-300)
//
//        binding.button2.setOnClickListener {
//            findNavController().navigate(R.id.registerOnlineBusinessFragment)
//        }
    }

//    fun onRegisterBusinessButtonClicked(){
//        val registerBusinessFragment = requireParentFragment() as RegisterBusinessFragment
//        registerBusinessFragment.registerOnlineBusiness()
//    }
}