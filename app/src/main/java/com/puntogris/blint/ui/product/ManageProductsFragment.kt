package com.puntogris.blint.ui.product

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.map
import androidx.recyclerview.widget.LinearLayoutManager
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentManageProductsBinding
import com.puntogris.blint.model.Product
import com.puntogris.blint.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ManageProductsFragment : BaseFragment<FragmentManageProductsBinding>(R.layout.fragment_manage_products) {

    private val viewModel: ProductViewModel by viewModels()

    override fun initializeViews() {
        val manageProductsAdapter = ManageProductsAdapter{onProductClickListener(it)}
        binding.recyclerView.adapter = manageProductsAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        lifecycleScope.launchWhenStarted {
            viewModel.getAllProducts().collect {
                manageProductsAdapter.submitData(it)
            }
        }
    }

    private fun onProductClickListener(product:Product){
        findNavController().navigate(R.id.productFragment)
    }

    override fun onDestroyView() {
        binding.recyclerView.adapter = null
        super.onDestroyView()
    }
}