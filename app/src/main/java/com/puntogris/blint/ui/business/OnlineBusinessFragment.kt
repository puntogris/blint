package com.puntogris.blint.ui.business

import androidx.core.view.setPadding
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentOnlineBusinessBinding
import com.puntogris.blint.ui.base.BaseFragment

class OnlineBusinessFragment : BaseFragment<FragmentOnlineBusinessBinding>(R.layout.fragment_online_business) {

    override fun initializeViews() {
        binding.onlineBusinessFragment = this
        binding.animationView.setPadding(-400)
    }

    fun onRegisterBusinessButtonClicked(){
        val registerBusinessFragment = requireParentFragment() as RegisterBusinessFragment
        registerBusinessFragment.registerOnlineBusiness()
    }
}