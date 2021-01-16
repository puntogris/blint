package com.puntogris.blint.ui.business

import androidx.navigation.fragment.findNavController
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentBusinessWaitingRoomBinding
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.showShortSnackBar

class BusinessWaitingRoomFragment : BaseFragment<FragmentBusinessWaitingRoomBinding>(R.layout.fragment_business_waiting_room) {

    override fun initializeViews() {
        binding.fragment = this
    }

    fun onJoinBusinessClicked(){
        if (isCheckBoxChecked()) findNavController().navigate(R.id.joinBusinessFragment)
    }

    fun onRegisterBusinessClicked(){
        if(isCheckBoxChecked()) findNavController().navigate(R.id.registerBusinessFragment)
    }

    fun onReadMoreAboutPolicesClicked(){
        findNavController().navigate(R.id.termsConditionsFragment)
    }

    private fun isCheckBoxChecked(): Boolean{
        return if(binding.termsAndConditionsCheckBox.isChecked) true
        else{
            showShortSnackBar("Acepta nuestra condiciones para poder continuar.")
            false
        }
    }
}