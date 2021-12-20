package com.puntogris.blint.feature_store.presentation.product.manage

import android.Manifest
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.puntogris.blint.R
import com.puntogris.blint.common.presentation.base.BaseFragment
import com.puntogris.blint.common.utils.*
import com.puntogris.blint.databinding.FragmentManageProductsBinding
import com.puntogris.blint.feature_store.domain.model.product.ProductWithDetails
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class ManageProductsFragment :
    BaseFragment<FragmentManageProductsBinding>(R.layout.fragment_manage_products) {

    private val viewModel: ManageProductsViewModel by viewModels()
    private lateinit var scannerLauncher: ActivityResultLauncher<String>

    override fun initializeViews() {
        binding.fragment = this
        binding.viewModel = viewModel
        binding.productSearch.clearFocus()

        setupToolbar()
        setupProductsAdapter()
        setupScannerLauncher()
        setupResultListener()
    }

    private fun setupToolbar() {
        binding.toolbar.apply {
            registerToolbarBackButton(this)
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.action_add_manage_products -> {
                        findNavController().navigate(R.id.editProductFragment)
                    }
                    R.id.action_scanner_manage_products -> {
                        hideKeyboard()
                        scannerLauncher.launch(Manifest.permission.CAMERA)
                    }
                }
                true
            }

        }
    }

    private fun setupResultListener() {
        setFragmentResultListener(Keys.SCANNER_RESULT_KEY) { _, bundle ->
            bundle.getString(Keys.PRODUCT_BARCODE_KEY)?.let {
                viewModel.setQuery(it)
            }
        }
    }

    private fun setupScannerLauncher() {
        scannerLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission())
        { isGranted: Boolean ->
            if (isGranted) {
                val action =
                    ManageProductsFragmentDirections.actionGlobalScannerFragment(
                        returnResult = true
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
        launchAndRepeatWithViewLifecycle {
            viewModel.productsFlow.collect {
                adapter.submitData(it)
            }
        }
        launchAndRepeatWithViewLifecycle {
            showEmptyUiOnEmptyAdapter(adapter, binding.emptyUi)
        }
    }

    private fun onProductShortClickListener(product: ProductWithDetails) {
        hideKeyboard()
        val action =
            ManageProductsFragmentDirections.actionGlobalProductFragment(product)
        findNavController().navigate(action)
    }

    private fun onProductLongClickListener(product: ProductWithDetails) {
        showOrderPickerAndNavigate(product.product)
    }

    override fun onDestroyView() {
        with(binding) {
            toolbar.setNavigationOnClickListener(null)
            productSearch.clearFocus()
            recyclerView.adapter = null
        }
        super.onDestroyView()
    }
}