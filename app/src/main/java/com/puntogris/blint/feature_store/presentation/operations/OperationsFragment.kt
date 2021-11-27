package com.puntogris.blint.feature_store.presentation.operations

import androidx.navigation.fragment.findNavController
import com.puntogris.blint.R
import com.puntogris.blint.common.presentation.base.BaseFragment
import com.puntogris.blint.common.utils.UiInterface
import com.puntogris.blint.databinding.FragmentOperationsBinding

class OperationsFragment : BaseFragment<FragmentOperationsBinding>(R.layout.fragment_operations) {

    override fun initializeViews() {
        binding.fragment = this
        UiInterface.registerUi()
    }

    fun onGeneralPriceChangeClicked() {
        findNavController().navigate(R.id.generalPriceChangeFragment)
    }

    fun onImportProductsClicked() {
        findNavController().navigate(R.id.importProductsFragment)
    }
}