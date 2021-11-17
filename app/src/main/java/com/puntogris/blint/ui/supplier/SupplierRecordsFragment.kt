package com.puntogris.blint.ui.supplier

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentSupplierRecordsBinding
import com.puntogris.blint.model.Record
import com.puntogris.blint.model.Supplier
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.ui.orders.manage.RecordsAdapter
import com.puntogris.blint.utils.Constants.SUPPLIER_DATA_KEY
import com.puntogris.blint.utils.takeArgsIfNotNull
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class SupplierRecordsFragment :
    BaseFragment<FragmentSupplierRecordsBinding>(R.layout.fragment_supplier_records) {

    private val viewModel: SupplierViewModel by viewModels()

    override fun initializeViews() {
        val recordsAdapter = RecordsAdapter { onRecordClickListener(it) }

        binding.recyclerView.apply {
            adapter = recordsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        takeArgsIfNotNull<Supplier>(SUPPLIER_DATA_KEY) {
            lifecycleScope.launchWhenStarted {
                viewModel.getSupplierRecords(it.supplierId).collect {
                    recordsAdapter.submitData(it)
                }
            }
        }
    }

    private fun onRecordClickListener(record: Record) {
        (requireParentFragment() as SupplierFragment).navigateToInfoRecord(record)
    }
}