package com.puntogris.blint.ui.client

import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentClientRecordsBinding
import com.puntogris.blint.model.Client
import com.puntogris.blint.model.Record
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.ui.orders.manage.RecordsAdapter
import com.puntogris.blint.utils.Constants.CLIENT_DATA_KEY
import com.puntogris.blint.utils.takeArgsIfNotNull
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ClientRecordsFragment :BaseFragment<FragmentClientRecordsBinding>(R.layout.fragment_client_records) {

    private val viewModel: ClientViewModel by viewModels()

    override fun initializeViews() {
        val recordsAdapter = RecordsAdapter { onRecordClickListener(it) }

        binding.recyclerView.apply {
            adapter = recordsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        takeArgsIfNotNull<Client>(CLIENT_DATA_KEY){
            lifecycleScope.launch {
                viewModel.getClientsRecords(it.clientId).collect {
                    recordsAdapter.submitData(it)
                }
            }
        }
    }

    private fun onRecordClickListener(record: Record){
        (requireParentFragment() as ClientFragment).navigateToInfoRecord(record)
    }
}