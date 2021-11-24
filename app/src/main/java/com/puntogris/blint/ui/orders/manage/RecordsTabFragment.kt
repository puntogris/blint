package com.puntogris.blint.ui.orders.manage

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentRecordsTabBinding
import com.puntogris.blint.model.order.Record
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.launchAndRepeatWithViewLifecycle
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
            binding.recyclerView.adapter = it
            subscribeUi(it)
        }
    }

    private fun subscribeUi(adapter: RecordsAdapter) {
        launchAndRepeatWithViewLifecycle {
            viewModel.getRecords().collect {
                adapter.submitData(it)
            }
        }
    }

    private fun onRecordClickedListener(record: Record) {
        val action =
            ManageOrdersFragmentDirections.actionManageOrdersFragmentToRecordInfoBottomSheet(record)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        binding.recyclerView.adapter = null
        super.onDestroyView()
    }

}