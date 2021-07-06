package com.puntogris.blint.ui.product

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.chip.Chip
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentDataProductBinding
import com.puntogris.blint.model.ProductWithSuppliersCategories
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.getParentFab
import com.puntogris.blint.utils.gone
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
            getParcelable<ProductWithSuppliersCategories>("product_key")?.let {
                lifecycleScope.launch {
                    viewModel.setProductData(it.product)

                    if (!it.suppliers.isNullOrEmpty()){
                        it.suppliers?.forEach { supplier->
                            Chip(requireContext()).apply {
                                text = supplier.companyName
                                setOnClickListener {
                                    (requireParentFragment() as ProductFragment).navigateToSupplier(supplier.supplierId)
                                }
                                binding.supplierChipGroup.addView(this)
                            }
                        }
                    }
                    if (!it.categories.isNullOrEmpty()){
                        it.categories?.forEach { category ->
                            Chip(requireContext()).apply {
                                text = category.name
                                setOnClickListener {

                                }
                                binding.categoriesChipGroup.addView(this)
                            }
                        }
                    }
                }
            }
        }
    }
}