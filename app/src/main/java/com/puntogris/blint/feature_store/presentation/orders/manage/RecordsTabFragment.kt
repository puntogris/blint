package com.puntogris.blint.feature_store.presentation.orders.manage

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.puntogris.blint.R
import com.puntogris.blint.common.utils.Constants
import com.puntogris.blint.common.utils.launchAndRepeatWithViewLifecycle
import com.puntogris.blint.common.utils.showEmptyUiOnEmptyAdapter
import com.puntogris.blint.common.utils.viewBinding
import com.puntogris.blint.databinding.FragmentRecordsTabBinding
import com.puntogris.blint.feature_store.domain.model.order.Record
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecordsTabFragment : Fragment(R.layout.fragment_records_tab) {

    private val viewModel: ManageOrdersViewModel by viewModels(ownerProducer = { requireParentFragment() })

    private val binding by viewBinding(FragmentRecordsTabBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecordsAdapter()
    }

    private fun setupRecordsAdapter() {
        val adapter = RecordsAdapter { onRecordClickedListener(it) }
        binding.recordsTabRecyclerView.adapter = adapter
        subscribeUi(adapter)
    }

    private fun subscribeUi(adapter: RecordsAdapter) {
        launchAndRepeatWithViewLifecycle {
            viewModel.getRecords().collect {
                adapter.submitData(it)
            }
        }
        launchAndRepeatWithViewLifecycle {
            showEmptyUiOnEmptyAdapter(adapter, binding.recordsTabEmptyUi.root)
        }
    }

    private fun onRecordClickedListener(record: Record) {
        val action = if (record.type == Constants.INITIAL) {
            ManageOrdersFragmentDirections.actionGlobalInitialRecordBottomSheet(record)
        } else {
            ManageOrdersFragmentDirections.actionGlobalOrderFragment(orderId = record.orderId)
        }
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        binding.recordsTabRecyclerView.adapter = null
        super.onDestroyView()
    }
}
