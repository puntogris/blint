package com.puntogris.blint.feature_store.presentation.product.details

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.puntogris.blint.R
import com.puntogris.blint.common.utils.launchAndRepeatWithViewLifecycle
import com.puntogris.blint.common.utils.showEmptyUiOnEmptyAdapter
import com.puntogris.blint.common.utils.viewBinding
import com.puntogris.blint.databinding.FragmentProductRecordsBinding
import com.puntogris.blint.feature_store.domain.model.order.Record
import com.puntogris.blint.feature_store.presentation.orders.manage.RecordsAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductRecordsFragment :
    Fragment(R.layout.fragment_product_records) {

    private val viewModel: ProductViewModel by viewModels(ownerProducer = { requireParentFragment() })

    private val binding by viewBinding(FragmentProductRecordsBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
