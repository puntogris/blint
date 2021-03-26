package com.puntogris.blint.ui.product

import android.view.Menu
import android.view.MenuItem
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.maxkeppeler.sheets.info.InfoSheet
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentManageProductsBinding
import com.puntogris.blint.model.Product
import com.puntogris.blint.ui.base.BaseFragmentOptions
import com.puntogris.blint.ui.supplier.SupplierFragmentDirections
import com.puntogris.blint.utils.getParentFab
import com.puntogris.blint.utils.showLongSnackBarAboveFab
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.system.measureTimeMillis

@AndroidEntryPoint
class ManageProductsFragment : BaseFragmentOptions<FragmentManageProductsBinding>(R.layout.fragment_manage_products) {

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

        getParentFab().setOnClickListener {
            findNavController().navigate(R.id.editProductFragment)
        }
    }

    private fun onProductClickListener(product: Product){
        val action = ManageProductsFragmentDirections.actionManageProductsFragmentToProductFragment(product.productId)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        binding.recyclerView.adapter = null
        super.onDestroyView()
    }

    override fun setUpMenuOptions(menu: Menu) {
        menu.findItem(R.id.manageProductFragmentMenu).isVisible = true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.newProduct -> {
                findNavController().navigate(R.id.editProductFragment)
                true
            }
            R.id.manageCategories -> {
                findNavController().navigate(R.id.manageCategoriesFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}