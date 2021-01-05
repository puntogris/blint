package com.puntogris.blint.ui.client

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.map
import androidx.recyclerview.widget.LinearLayoutManager
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentManageClientsBinding
import com.puntogris.blint.model.Client
import com.puntogris.blint.model.Product
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.ui.product.ManageProductsAdapter
import com.puntogris.blint.ui.product.ProductViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class ManageClientsFragment : BaseFragment<FragmentManageClientsBinding>(R.layout.fragment_manage_clients) {

    private val viewModel: ClientViewModel by viewModels()

    override fun initializeViews() {
        val manageProductsAdapter = ManageClientsAdapter{onClientClickListener(it)}
        binding.recyclerView.adapter = manageProductsAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        lifecycleScope.launchWhenStarted {
            viewModel.getAllClients().collect {
                manageProductsAdapter.submitData(it)
            }
        }
    }

    private fun onClientClickListener(client:Client){

    }
}