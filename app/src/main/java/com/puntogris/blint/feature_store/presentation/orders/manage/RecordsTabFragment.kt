package com.puntogris.blint.feature_store.presentation.orders.manage

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.puntogris.blint.R
import com.puntogris.blint.common.presentation.base.BaseFragment
import com.puntogris.blint.common.utils.launchAndRepeatWithViewLifecycle
import com.puntogris.blint.common.utils.showEmptyUiOnEmptyAdapter
import com.puntogris.blint.databinding.FragmentRecordsTabBinding
import com.puntogris.blint.feature_store.domain.model.order.Record
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class RecordsTabFragment : BaseFragment<FragmentRecordsTabBinding>(R.layout.fragment_records_tab) {

    private val viewModel: ManageOrdersViewModel by viewModels(ownerProducer = { requireParentFragment() })

    override fun initializeViews() {
        setupRecordsAdapter()
    }

    private fun setupRecordsAdapter() {
        RecordsAdapter { onRecordClickedListener(it) }.let {
            binding.recordsTabRecyclerView.adapter = it
            subscribeUi(it)
        }
    }

    private fun subscribeUi(adapter: RecordsAdapter) {
        launchAndRepeatWithViewLifecycle {
            viewModel.getRecords().collect {
                adapter.submitData(it)
            }
        }
        launchAndRepeatWithViewLifecycle {
            showEmptyUiOnEmptyAdapter(adapter, binding.recordsTabEmptyUi)
        }
    }

    private fun onRecordClickedListener(record: Record) {
        val action =
            ManageOrdersFragmentDirections.actionGlobalOrderFragment(orderId = record.orderId)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        binding.recordsTabRecyclerView.adapter = null
        super.onDestroyView()
    }

}