package com.puntogris.blint.ui.product.manage

import android.Manifest
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentManageProductsBinding
import com.puntogris.blint.model.product.ProductWithDetails
import com.puntogris.blint.ui.base.BaseFragmentOptions
import com.puntogris.blint.utils.Constants.PRODUCT_BARCODE_KEY
import com.puntogris.blint.utils.Constants.SIMPLE_ORDER_KEY
import com.puntogris.blint.utils.UiInterface
import com.puntogris.blint.utils.hideKeyboard
import com.puntogris.blint.utils.showOrderPickerAndNavigate
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ManageProductsFragment :
    BaseFragmentOptions<FragmentManageProductsBinding>(R.layout.fragment_manage_products) {

    private val viewModel: ManageProductsViewModel by viewModels()
    lateinit var scannerLauncher: ActivityResultLauncher<String>

    override fun initializeViews() {
        UiInterface.registerUi(showToolbar = false, showAppBar = true, showFab = true) {
            findNavController().navigate(R.id.editProductFragment)
        }

        binding.productSearch.clearFocus()
        binding.searchToolbar.setNavigationOnClickListener { findNavController().navigateUp() }
        binding.fragment = this

        setupProductsAdapter()
        setupScannerLauncher()

        binding.productSearch.addTextChangedListener {
            viewModel.setQuery(it.toString())
        }


        findNavController().currentBackStackEntry?.savedStateHandle?.apply {
            getLiveData<String>(PRODUCT_BARCODE_KEY).observe(viewLifecycleOwner) {
                it?.let { code -> binding.productSearch.setText(code) }
            }

            getLiveData<Boolean>(SIMPLE_ORDER_KEY).observe(viewLifecycleOwner) {
                //   if (it) manageProductsAdapter.refresh()
            }
        }
    }

    private fun setupScannerLauncher() {
        scannerLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission())
        { isGranted: Boolean ->
            if (isGranted) {
                val action =
                    ManageProductsFragmentDirections.actionManageProductsFragmentToScannerFragment(
                        1
                    )
                findNavController().navigate(action)
            } else UiInterface.showSnackBar(getString(R.string.snack_require_camera_permission))
        }
    }

    private fun setupProductsAdapter() {
        ManageProductsAdapter(
            { onProductShortClickListener(it) },
            { onProductLongClickListener(it) }
        ).let {
            binding.recyclerView.adapter = it
            subscribeUi(it)
        }
    }

    private fun subscribeUi(adapter: ManageProductsAdapter) {
        viewModel.productsLiveData.observe(viewLifecycleOwner) {
            adapter.submitData(viewLifecycleOwner.lifecycle, it)
        }
    }

    fun onScanBarcodeClicked() {
        hideKeyboard()
        scannerLauncher.launch(Manifest.permission.CAMERA)
    }

    private fun onProductShortClickListener(product: ProductWithDetails) {
        hideKeyboard()
        val action =
            ManageProductsFragmentDirections.actionManageProductsFragmentToProductFragment(product)
        findNavController().navigate(action)
    }

    private fun onProductLongClickListener(product: ProductWithDetails) {
        showOrderPickerAndNavigate(product.product)
    }

    override fun onDestroyView() {
        with(binding) {
            searchToolbar.setNavigationOnClickListener(null)
            productSearch.clearFocus()
            recyclerView.adapter = null
        }
        super.onDestroyView()
    }
}