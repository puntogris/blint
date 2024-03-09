package com.puntogris.blint.feature_store.presentation.product.details

import androidx.fragment.app.viewModels
import com.puntogris.blint.R
import com.puntogris.blint.common.presentation.base.BaseFragment
import com.puntogris.blint.common.utils.launchAndRepeatWithViewLifecycle
import com.puntogris.blint.common.utils.showEmptyUiOnEmptyAdapter
import com.puntogris.blint.databinding.FragmentProductRecordsBinding
import com.puntogris.blint.feature_store.domain.model.order.Record
import com.puntogris.blint.feature_store.presentation.orders.manage.RecordsAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductRecordsFragment :
    BaseFragment<FragmentProductRecordsBinding>(R.layout.fragment_product_records) {

    private val viewModel: ProductViewModel by viewModels(ownerProducer = { requireParentFragment() })

    override fun initializeViews() {
        setupRecordsAdapter()
    }

    private fun setupRecordsAdapter() {
        val adapter = RecordsAdapter { onRecordClickListener(it) }
        binding.productRecordsRecyclerView.adapter = adapter
        subscribeUi(adapter)
    }

    private fun subscribeUi(adapter: RecordsAdapter) {
        launchAndRepeatWithViewLifecycle {
            viewModel.productRecords.collect {
                adapter.submitData(it)
            }
        }
        launchAndRepeatWithViewLifecycle {
            showEmptyUiOnEmptyAdapter(adapter, binding.productRecordsEmptyUi)
        }
    }

    private fun onRecordClickListener(record: Record) {
        (requireParentFragment() as ProductFragment).navigateToInfoRecord(record)
    }

    override fun onDestroyView() {
        binding.productRecordsRecyclerView.adapter = null
        super.onDestroyView()
    }
}
