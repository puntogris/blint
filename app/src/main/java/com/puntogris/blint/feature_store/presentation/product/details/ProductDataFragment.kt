package com.puntogris.blint.feature_store.presentation.product.details

import androidx.fragment.app.viewModels
import com.puntogris.blint.R
import com.puntogris.blint.common.presentation.base.BaseFragment
import com.puntogris.blint.databinding.FragmentDataProductBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductDataFragment :
    BaseFragment<FragmentDataProductBinding>(R.layout.fragment_data_product) {

    private val viewModel: ProductViewModel by viewModels(ownerProducer = { requireParentFragment() })

    override fun initializeViews() {
        binding.fragment = this
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

    }
}