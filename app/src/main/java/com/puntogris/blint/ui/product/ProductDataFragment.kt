package com.puntogris.blint.ui.product

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentDataProductBinding
import com.puntogris.blint.model.ProductWithSuppliersCategories
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.Constants.PRODUCT_DATA_KEY
import com.puntogris.blint.utils.takeArgsIfNotNull
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProductDataFragment : BaseFragment<FragmentDataProductBinding>(R.layout.fragment_data_product) {

    private val viewModel: ProductViewModel by viewModels(ownerProducer = {requireParentFragment()} )

    override fun initializeViews() {
        binding.fragment = this
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        takeArgsIfNotNull<ProductWithSuppliersCategories>(PRODUCT_DATA_KEY){
            viewModel.setProductData(it)
        }
    }
}