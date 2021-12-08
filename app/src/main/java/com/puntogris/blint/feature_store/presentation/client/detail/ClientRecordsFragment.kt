package com.puntogris.blint.feature_store.presentation.client.detail

import androidx.fragment.app.viewModels
import com.puntogris.blint.R
import com.puntogris.blint.common.presentation.base.BaseFragment
import com.puntogris.blint.common.utils.launchAndRepeatWithViewLifecycle
import com.puntogris.blint.databinding.FragmentClientRecordsBinding
import com.puntogris.blint.feature_store.domain.model.order.Record
import com.puntogris.blint.feature_store.presentation.orders.manage.RecordsAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class ClientRecordsFragment :
    BaseFragment<FragmentClientRecordsBinding>(R.layout.fragment_client_records) {

    private val viewModel: ClientViewModel by viewModels(ownerProducer = { requireParentFragment() })

    override fun initializeViews() {
        setupRecordsAdapter()
    }

    private fun setupRecordsAdapter() {
        RecordsAdapter { onRecordClickListener(it) }.let {
            binding.recyclerView.adapter = it
            subscribeUi(it)
        }
    }

    private fun subscribeUi(adapter: RecordsAdapter) {
        launchAndRepeatWithViewLifecycle {
            viewModel.clientsRecords.collect {
                adapter.submitData(it)
            }
        }
    }

    private fun onRecordClickListener(record: Record) {
        (requireParentFragment() as ClientFragment).navigateToInfoRecord(record)
    }
}