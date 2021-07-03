package com.puntogris.blint.ui.supplier

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentSupplierRecordsBinding
import com.puntogris.blint.model.Record
import com.puntogris.blint.model.Supplier
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.ui.orders.ProductsRecordsAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class SupplierRecordsFragment : BaseFragment<FragmentSupplierRecordsBinding>(R.layout.fragment_supplier_records) {

    private val viewModel:SupplierViewModel by viewModels()

    override fun initializeViews() {
        val productsRecordsAdapter = ProductsRecordsAdapter { onRecordClickListener(it) }
        binding.recyclerView.adapter = productsRecordsAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        arguments?.takeIf { it.containsKey("supplier_key") }?.apply {
            getParcelable<Supplier>("supplier_key")?.let {
                lifecycleScope.launchWhenStarted {
                    viewModel.getSupplierRecords(it.supplierId).collect {
                        productsRecordsAdapter.submitData(it)
                    }
                }
            }
        }
    }

    private fun onRecordClickListener(record: Record){
        val supplierFragment = requireParentFragment() as SupplierFragment
        supplierFragment.navigateToInfoRecord(record)
    }
}