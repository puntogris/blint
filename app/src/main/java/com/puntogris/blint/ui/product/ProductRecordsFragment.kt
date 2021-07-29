package com.puntogris.blint.ui.product

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentProductRecordsBinding
import com.puntogris.blint.model.ProductWithSuppliersCategories
import com.puntogris.blint.model.Record
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.ui.orders.manage.RecordsAdapter
import com.puntogris.blint.utils.Constants.PRODUCT_DATA_KEY
import com.puntogris.blint.utils.registerUiInterface
import com.puntogris.blint.utils.showOrderPickerAndNavigate
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProductRecordsFragment : BaseFragment<FragmentProductRecordsBinding>(R.layout.fragment_product_records) {

    private val viewModel: ProductViewModel by viewModels(ownerProducer = {requireParentFragment()} )

    override fun initializeViews() {

        val productsRecordsAdapter = RecordsAdapter { onRecordClickListener(it) }
        binding.recyclerView.adapter = productsRecordsAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        arguments?.takeIf { it.containsKey(PRODUCT_DATA_KEY) }?.apply {
            getParcelable<ProductWithSuppliersCategories>(PRODUCT_DATA_KEY)?.let { product ->
                lifecycleScope.launch {
                    viewModel.getProductRecords(product.product.productId).collect {
                        productsRecordsAdapter.submitData(it)
                    }
                }
            }
        }
    }

    private fun onRecordClickListener(record: Record){
        (requireParentFragment() as ProductFragment).navigateToInfoRecord(record)
    }
}