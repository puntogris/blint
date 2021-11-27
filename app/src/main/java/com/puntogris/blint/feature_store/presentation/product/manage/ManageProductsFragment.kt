package com.puntogris.blint.feature_store.presentation.product.manage

import android.Manifest
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.puntogris.blint.R
import com.puntogris.blint.common.presentation.base.BaseFragmentOptions
import com.puntogris.blint.common.utils.*
import com.puntogris.blint.databinding.FragmentManageProductsBinding
import com.puntogris.blint.feature_store.domain.model.product.ProductWithDetails
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ManageProductsFragment :
    BaseFragmentOptions<FragmentManageProductsBinding>(R.layout.fragment_manage_products) {

    private val viewModel: ManageProductsViewModel by viewModels()
    private lateinit var scannerLauncher: ActivityResultLauncher<String>

    override fun initializeViews() {
        UiInterface.registerUi(showToolbar = false, showAppBar = true, showFab = true) {
            findNavController().navigate(R.id.editProductFragment)
        }
        binding.fragment = this
        binding.viewModel = viewModel
        binding.productSearch.clearFocus()
        registerToolbarBackButton(binding.searchToolbar)

        setupProductsAdapter()
        setupScannerLauncher()
        setupResultListener()
    }

    private fun setupResultListener() {
        setFragmentResultListener(Constants.SCANNER_RESULT_KEY) { _, bundle ->
            bundle.getString(Constants.PRODUCT_BARCODE_KEY)?.let {
                viewModel.setQuery(it)
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