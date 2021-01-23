package com.puntogris.blint.ui.product

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.chip.Chip
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
                    val product = viewModel.getProductWithSuppliers(it)
                    viewModel.setProductData(product.product)

                    if (product.suppliers.isNotEmpty()){
                        product.suppliers.forEach { supplier->
                            Chip(requireContext()).apply {
                                text = supplier.companyName
                                setOnClickListener {
                                    (requireParentFragment() as ProductFragment).navigateToSupplier(supplier.supplierId)
                                }
                                binding.supplierChipGroup.addView(this)
                            }
                        }
                    }
                }
            }
        }
    }
}