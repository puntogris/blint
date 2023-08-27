package com.puntogris.blint.feature_store.presentation.trader.detail

import androidx.fragment.app.viewModels
import com.puntogris.blint.R
import com.puntogris.blint.common.presentation.base.BaseFragment
import com.puntogris.blint.common.utils.launchAndRepeatWithViewLifecycle
import com.puntogris.blint.common.utils.showEmptyUiOnEmptyAdapter
import com.puntogris.blint.databinding.FragmentTraderRecordsBinding
import com.puntogris.blint.feature_store.domain.model.order.Record
import com.puntogris.blint.feature_store.presentation.orders.manage.RecordsAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TraderRecordsFragment :
    BaseFragment<FragmentTraderRecordsBinding>(R.layout.fragment_trader_records) {

    private val viewModel: TraderViewModel by viewModels(ownerProducer = { requireParentFragment() })

    override fun initializeViews() {
        setupRecordsAdapter()
    }

    private fun setupRecordsAdapter() {
        RecordsAdapter { onRecordClickListener(it) }.let {
            binding.traderRecordsRecyclerView.adapter = it
            subscribeUi(it)
        }
    }

    private fun subscribeUi(adapter: RecordsAdapter) {
        launchAndRepeatWithViewLifecycle {
            viewModel.traderRecords.collect {
                adapter.submitData(it)
            }
        }
        launchAndRepeatWithViewLifecycle {
            showEmptyUiOnEmptyAdapter(adapter, binding.traderRecordsEmptyUi)
        }
    }

    private fun onRecordClickListener(record: Record) {
        (requireParentFragment() as TraderFragment).navigateToInfoRecord(record)
    }

    override fun onDestroyView() {
        binding.traderRecordsRecyclerView.adapter = null
        super.onDestroyView()
    }
}