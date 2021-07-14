package com.puntogris.blint.ui.business

import androidx.core.view.setPadding
import androidx.navigation.fragment.findNavController
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentOnlineBusinessBinding
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.setUpUi

class OnlineBusinessFragment : BaseFragment<FragmentOnlineBusinessBinding>(R.layout.fragment_online_business) {

    override fun initializeViews() {
        setUpUi(showAppBar = false)
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