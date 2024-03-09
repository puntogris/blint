package com.puntogris.blint.feature_store.presentation.product.details

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.puntogris.blint.R
import com.puntogris.blint.common.utils.viewBinding
import com.puntogris.blint.databinding.FragmentDataProductBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductDataFragment : Fragment(R.layout.fragment_data_product) {

    private val viewModel: ProductViewModel by viewModels(ownerProducer = { requireParentFragment() })

    private val binding by viewBinding(FragmentDataProductBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.fragment = this
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
    }
}
