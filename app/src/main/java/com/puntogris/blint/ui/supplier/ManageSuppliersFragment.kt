package com.puntogris.blint.ui.supplier

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentManageSuppliersBinding
import com.puntogris.blint.model.Client
import com.puntogris.blint.model.Supplier
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.ui.client.ClientViewModel
import com.puntogris.blint.ui.client.ManageClientsAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class ManageSuppliersFragment : BaseFragment<FragmentManageSuppliersBinding>(R.layout.fragment_manage_suppliers) {

    private val viewModel: SupplierViewModel by viewModels()

    override fun initializeViews() {
        val manageProductsAdapter = ManageSuppliersAdapter{ onSupplierClickListener(it)}
        binding.recyclerView.adapter = manageProductsAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        lifecycleScope.launchWhenStarted {
            viewModel.getAllSuppliers().collect {
                manageProductsAdapter.submitData(it)
            }
        }
    }

    private fun onSupplierClickListener(supplier: Supplier){

    }

    override fun onDestroyView() {
        binding.recyclerView.adapter = null
        super.onDestroyView()
    }
}