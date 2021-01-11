package com.puntogris.blint.ui.product

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentDataProductBinding
import com.puntogris.blint.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProductDataFragment : BaseFragment<FragmentDataProductBinding>(R.layout.fragment_data_product) {

    private val viewModel: ProductViewModel by viewModels()

    override fun initializeViews() {
        binding.fragment = this
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        arguments?.takeIf { it.containsKey("product_key") }?.apply {
            getInt("product_key").let {
                lifecycleScope.launch {
                    val product = viewModel.getProduct(it)
                    viewModel.setProductData(product)
                }
            }
        }

    }
}