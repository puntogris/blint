package com.puntogris.blint.ui.client

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentClientRecordsBinding
import com.puntogris.blint.model.Client
import com.puntogris.blint.model.Record
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.ui.orders.ProductsRecordsAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class ClientRecordsFragment : BaseFragment<FragmentClientRecordsBinding>(R.layout.fragment_client_records) {

    private val viewModel: ClientViewModel by viewModels()

    override fun initializeViews() {
        val productsRecordsAdapter = ProductsRecordsAdapter { onRecordClickListener(it) }
        binding.recyclerView.adapter = productsRecordsAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        arguments?.takeIf { it.containsKey("client_key") }?.apply {
            getParcelable<Client>("client_key")?.let {
                lifecycleScope.launchWhenStarted {
                    viewModel.getClientsRecords(it.clientId).collect {
                        productsRecordsAdapter.submitData(it)
                    }
                }
            }
        }
    }

    private fun onRecordClickListener(record: Record){
        (requireParentFragment() as ClientFragment).navigateToInfoRecord(record)
    }
}