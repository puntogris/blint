package com.puntogris.blint.ui.orders.detailed_order

import android.Manifest
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.puntogris.blint.DetailedOrderGraphNavDirections
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentOrderProductsBinding
import com.puntogris.blint.model.order.NewRecord
import com.puntogris.blint.model.product.Product
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class OrderProductsFragment :
    BaseFragment<FragmentOrderProductsBinding>(R.layout.fragment_order_products) {

    private val viewModel: NewOrderViewModel by navGraphViewModels(R.id.detailedOrderGraphNav) { defaultViewModelProviderFactory }
    private lateinit var scannerLauncher: ActivityResultLauncher<String>

    override fun initializeViews() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.fragment = this
        binding.viewModel = viewModel
        registerToolbarBackButton(binding.searchToolbar)

        UiInterface.registerUi(
            showFab = true,
            showAppBar = false,
            showToolbar = false,
            showFabCenter = false,
            fabIcon = R.drawable.ic_baseline_arrow_forward_24
        ) { navigateToReviewOrder() }

        setupProductsSearchAdapter()
        setupOderProductAdapter()
        setupScannerLauncher()
        setupResultListener()
    }

    private fun setupProductsSearchAdapter() {
        SearchProductAdapter { onProductAdded(it) }.let {
            binding.productSearchRecyclerView.adapter = it
            subscribeSearchUi(it)
        }
    }

    private fun subscribeSearchUi(adapter: SearchProductAdapter) {
        viewModel.productsLiveData.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }

    private fun setupOderProductAdapter() {
        OrderProductsAdapter(
            requireContext(),
            { onDataChanged() },
            { deleteListener(it) }
        ).let {
            binding.recyclerView.adapter = it
            subscribeProductRecords(it)
        }
    }

    private fun subscribeProductRecords(adapter: OrderProductsAdapter) {
        launchAndRepeatWithViewLifecycle {
            viewModel.newOrder.collectLatest {
                adapter.submitList(it.newRecords)
            }
        }
    }

    private fun setupResultListener() {
        setFragmentResultListener(Constants.SCANNER_RESULT_KEY) { _, bundle ->
            bundle.getString(Constants.PRODUCT_BARCODE_KEY)?.let {
                binding.productSearch.setText(it)
            }
        }
    }

    private fun setupScannerLauncher() {
        scannerLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission())
            { isGranted: Boolean ->
                if (isGranted) {
                    val action = DetailedOrderGraphNavDirections.actionGlobalScannerFragment(1)
                    findNavController().navigate(action)
                } else UiInterface.showSnackBar(getString(R.string.snack_require_camera_permission))
            }
    }

    fun onScanBarcodeButtonClicked() {
        scannerLauncher.launch(Manifest.permission.CAMERA)
    }

    private fun deleteListener(newRecord: NewRecord) {
        viewModel.removeProduct(newRecord)
        UiInterface.showSnackBar(
            getString(R.string.product_deleted),
            actionText = R.string.action_undo
        ) {
            viewModel.addProduct(newRecord)
        }
    }

    private fun onDataChanged() {
        binding.orderTotal.setOrderTotal(viewModel.newOrder.value.newRecords)
    }

    private fun onProductAdded(product: Product) {
        viewModel.addProduct(product)
        binding.productSearch.setText("")
        binding.productSearch.clearFocus()
        hideKeyboard()
    }

    private fun navigateToReviewOrder() {
        println(viewModel.newOrder.value.newRecords)
        if (viewModel.areProductsValid()) {
            findNavController().navigate(R.id.reviewRecordFragment)
        } else {
            UiInterface.showSnackBar(getString(R.string.order_needs_products))
        }
    }

    override fun onDestroyView() {
        binding.recyclerView.adapter = null
        binding.productSearchRecyclerView.adapter = null
        super.onDestroyView()
    }
}