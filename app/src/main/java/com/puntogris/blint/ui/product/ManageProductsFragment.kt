package com.puntogris.blint.ui.product

import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.MarginLayoutParamsCompat
import androidx.core.view.isVisible
import androidx.core.view.marginTop
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.transition.MaterialSharedAxis
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentManageProductsBinding
import com.puntogris.blint.model.ProductWithSuppliersCategories
import com.puntogris.blint.ui.base.BaseFragmentOptions
import com.puntogris.blint.ui.nav.BottomNavDrawerFragment
import com.puntogris.blint.ui.nav.ShowHideFabStateAction
import com.puntogris.blint.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ManageProductsFragment : BaseFragmentOptions<FragmentManageProductsBinding>(R.layout.fragment_manage_products) {

    private val viewModel: ProductViewModel by viewModels()
    private lateinit var manageProductsAdapter : ManageProductsAdapter

    override fun initializeViews() {
        binding.productSearch.clearFocus()
        binding.searchToolbar.setNavigationOnClickListener { findNavController().navigateUp() }

        binding.fragment = this


        setUpUi(showToolbar = false, showAppBar = true, showFab = true){
            findNavController().navigate(R.id.editProductFragment)
        }

        requireActivity().findViewById<FragmentContainerView>(R.id.bottom_nav_drawer).getFragment<BottomNavDrawerFragment>()
            ?.addOnStateChangedAction(ShowHideFabStateAction(getParentFab(), true))

        manageProductsAdapter = ManageProductsAdapter { onProductClickListener(it) }
        binding.recyclerView.adapter = manageProductsAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        lifecycleScope.launchWhenStarted {
            searchProductAndFillAdapter()
        }

        binding.apply {
            productSearch.setOnFocusChangeListener { view, b ->
                btn1.visible()
                btn2.visible()
                btn3.visible()
            }
        }

        binding.productSearch.addTextChangedListener {
            lifecycleScope.launch {
                it.toString().let {
                    if (it.isBlank()) searchProductAndFillAdapter()
                    else searchProductWithNameAndFillAdapter(it)
                }
            }
        }

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<String>("key")?.observe(
            viewLifecycleOwner) {
            it?.let { code ->
                binding.productSearch.setText(code)
                lifecycleScope.launch {
                    searchProductWithNameAndFillAdapter(code)
                }
            }
        }
    }

    private suspend fun searchProductAndFillAdapter(){
        viewModel.getProductsPaging().collect {
            manageProductsAdapter.submitData(it)
        }
    }

    private suspend fun searchProductWithNameAndFillAdapter(text: String){
        viewModel.getProductWithName(text).collect { data ->
            manageProductsAdapter.submitData(data)
        }
    }

    private fun onProductClickListener(product: ProductWithSuppliersCategories){
        hideKeyboard()
        val action = ManageProductsFragmentDirections.actionManageProductsFragmentToProductFragment(product)
        findNavController().navigate(action)
    }

    fun onSearchCodeClicked(){
        val action = ManageProductsFragmentDirections.actionManageProductsFragmentToScannerFragment(1)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        binding.productSearch.clearFocus()
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