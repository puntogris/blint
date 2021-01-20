package com.puntogris.blint.ui.record

import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
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
        val productsRecordsAdapter = ProductsRecordsAdapter { onRecordClickListener(it) }
        binding.recyclerView.adapter = productsRecordsAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        lifecycleScope.launchWhenStarted {
            viewModel.getProductRecords().collect {
                productsRecordsAdapter.submitData(it)
            }
        }

        binding.productSearchText.addTextChangedListener {
            lifecycleScope.launch {
                viewModel.getRecordsWithName(it.toString()).collect {
                    productsRecordsAdapter.submitData(it)
                }
            }
        }
    }

    private fun onRecordClickListener(record: Record){
        val action = ManageRecordsFragmentDirections.actionManageRecordsFragmentToRecordInfoBottomSheet(record)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        binding.recyclerView.adapter = null
        super.onDestroyView()
    }
}