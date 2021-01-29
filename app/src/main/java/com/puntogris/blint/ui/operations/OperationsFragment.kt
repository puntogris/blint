package com.puntogris.blint.ui.operations

import androidx.navigation.fragment.findNavController
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentOperationsBinding
import com.puntogris.blint.ui.base.BaseFragment

class OperationsFragment : BaseFragment<FragmentOperationsBinding>(R.layout.fragment_operations) {

    override fun initializeViews() {
        binding.fragment = this
    }

    fun onAddMultipleProductsClicked(){
        findNavController().navigate(R.id.addMultipleProductsFragment)
    }

    fun onGeneralPriceChangeClicked(){
        findNavController().navigate(R.id.generalPriceChangeFragment)
    }
    fun onImportProductsClicked(){
        findNavController().navigate(R.id.importProductsFragment)
    }
}