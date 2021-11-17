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
import com.puntogris.blint.utils.takeArgsIfNotNull
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProductRecordsFragment :
    BaseFragment<FragmentProductRecordsBinding>(R.layout.fragment_product_records) {

    private val viewModel: ProductViewModel by viewModels(ownerProducer = { requireParentFragment() })

    override fun initializeViews() {
        val recordsAdapter = RecordsAdapter { onRecordClickListener(it) }

        binding.recyclerView.apply {
            adapter = recordsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        takeArgsIfNotNull<ProductWithSuppliersCategories>(PRODUCT_DATA_KEY) {
            lifecycleScope.launch {
                viewModel.getProductRecords(it.product.productId).collect {
                    recordsAdapter.submitData(it)
                }
            }
        }
    }

    private fun onRecordClickListener(record: Record) {
        (requireParentFragment() as ProductFragment).navigateToInfoRecord(record)
    }
}