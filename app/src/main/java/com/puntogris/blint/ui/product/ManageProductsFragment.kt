package com.puntogris.blint.ui.product

import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentManageProductsBinding
import com.puntogris.blint.model.Product
import com.puntogris.blint.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ManageProductsFragment : BaseFragment<FragmentManageProductsBinding>(R.layout.fragment_manage_products) {

    private val viewModel: ProductViewModel by viewModels()

    override fun initializeViews() {
        val manageProductsAdapter = ManageProductsAdapter { onProductClickListener(it) }
        binding.recyclerView.adapter = manageProductsAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        lifecycleScope.launchWhenStarted {
            viewModel.getAllProducts().collect {
                manageProductsAdapter.submitData(it)
            }
        }

        binding.productSearchText.addTextChangedListener {
            lifecycleScope.launch {
                viewModel.getProductWithName(it.toString()).collect {
                    manageProductsAdapter.submitData(it)
                }
            }
        }
    }

    private fun onProductClickListener(product: Product){
        val action = ManageProductsFragmentDirections.actionManageProductsFragmentToProductFragment(product.id)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        binding.recyclerView.adapter = null
        super.onDestroyView()
    }
}