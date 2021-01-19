package com.puntogris.blint.ui.record

import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentManageRecordsBinding
import com.puntogris.blint.model.Record
import com.puntogris.blint.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ManageRecordsFragment : BaseFragment<FragmentManageRecordsBinding>(R.layout.fragment_manage_records) {

    private val viewModel: RecordsViewModel by viewModels()

    override fun initializeViews() {
        val manageRecordsAdapter = ManageRecordsAdapter { onRecordClickListener(it) }
        binding.recyclerView.adapter = manageRecordsAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        lifecycleScope.launchWhenStarted {
            viewModel.getProductRecords().collect {
                manageRecordsAdapter.submitData(it)
            }
        }

        binding.productSearchText.addTextChangedListener {
            lifecycleScope.launch {
                viewModel.getRecordsWithName(it.toString()).collect {
                    manageRecordsAdapter.submitData(it)
                }
            }
        }
    }

    private fun onRecordClickListener(record: Record){

    }

    override fun onDestroyView() {
        binding.recyclerView.adapter = null
        super.onDestroyView()
    }
}