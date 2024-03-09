package com.puntogris.blint.feature_store.presentation.trader.detail

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.puntogris.blint.R
import com.puntogris.blint.common.utils.launchAndRepeatWithViewLifecycle
import com.puntogris.blint.common.utils.showEmptyUiOnEmptyAdapter
import com.puntogris.blint.common.utils.viewBinding
import com.puntogris.blint.databinding.FragmentTraderRecordsBinding
import com.puntogris.blint.feature_store.domain.model.order.Record
import com.puntogris.blint.feature_store.presentation.orders.manage.RecordsAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TraderRecordsFragment : Fragment(R.layout.fragment_trader_records) {

    private val viewModel: TraderViewModel by viewModels(ownerProducer = { requireParentFragment() })

    private val binding by viewBinding(FragmentTraderRecordsBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecordsAdapter()
    }

    private fun setupRecordsAdapter() {
        val adapter = RecordsAdapter { onRecordClickListener(it) }
        binding.traderRecordsRecyclerView.adapter = adapter
        subscribeUi(adapter)
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
