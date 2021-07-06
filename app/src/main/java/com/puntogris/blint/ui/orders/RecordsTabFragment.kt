package com.puntogris.blint.ui.orders

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentRecordsTabBinding
import com.puntogris.blint.model.Record
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.launchAndRepeatWithViewLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class RecordsTabFragment : BaseFragment<FragmentRecordsTabBinding>(R.layout.fragment_records_tab) {

    private val viewModel: OrdersViewModel by viewModels()

    override fun initializeViews() {
        val recordsAdapter = ProductsRecordsAdapter{onRecordClickedListener(it)}
        binding.recyclerView.apply {
            adapter = recordsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        launchAndRepeatWithViewLifecycle {
            viewModel.getBusinessRecords().collect {
                recordsAdapter.submitData(it)
            }
        }
    }

    private fun onRecordClickedListener(record:Record){

    }

    override fun onDestroyView() {
        binding.recyclerView.adapter = null
        super.onDestroyView()
    }

}