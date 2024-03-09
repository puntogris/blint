package com.puntogris.blint.feature_store.presentation.orders.create_order

import android.Manifest
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.puntogris.blint.CreateOrderGraphNavDirections
import com.puntogris.blint.R
import com.puntogris.blint.common.utils.Keys
import com.puntogris.blint.common.utils.UiInterface
import com.puntogris.blint.common.utils.hideKeyboard
import com.puntogris.blint.common.utils.launchAndRepeatWithViewLifecycle
import com.puntogris.blint.common.utils.registerToolbarBackButton
import com.puntogris.blint.common.utils.viewBinding
import com.puntogris.blint.databinding.FragmentOrderProductsBinding
import com.puntogris.blint.feature_store.domain.model.order.NewRecord
import com.puntogris.blint.feature_store.domain.model.product.Product
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class OrderProductsFragment : Fragment(R.layout.fragment_order_products) {

    private val viewModel: NewOrderViewModel by navGraphViewModels(R.id.createOrderGraphNav) {
        defaultViewModelProviderFactory
    }

    private val binding by viewBinding(FragmentOrderProductsBinding::bind)

    private lateinit var scannerLauncher: ActivityResultLauncher<String>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupProductsSearchAdapter()
        setupOderProductAdapter()
        setupScannerLauncher()
        setupResultListener()
        setupListeners()
    }

    private fun setupListeners() {
        binding.editTextProductsSearch.doAfterTextChanged { editable ->
            if (editable != null) {
                viewModel.setQuery(editable)
            }
        }
        binding.buttonDone.setOnClickListener {
            navigateToReviewOrder()
        }
    }

    private fun setupProductsSearchAdapter() {
        val adapter = SearchProductAdapter { onProductAdded(it) }
        binding.recyclerViewSearchProducts.adapter = adapter
        subscribeSearchUi(adapter)
    }

    private fun subscribeSearchUi(adapter: SearchProductAdapter) {
        viewModel.productsLiveData.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }

    private fun setupOderProductAdapter() {
        val adapter = OrderProductsAdapter(
            context = requireContext(),
            dataChangedListener = { onDataChanged() },
            deleteListener = { deleteListener(it) }
        )
        binding.recyclerViewOrderProducts.adapter = adapter
        subscribeProductRecords(adapter)
    }

    private fun subscribeProductRecords(adapter: OrderProductsAdapter) {
        launchAndRepeatWithViewLifecycle {
            viewModel.newOrder.collectLatest {
                adapter.submitList(it.newRecords)
            }
        }
    }

    private fun setupResultListener() {
        setFragmentResultListener(Keys.SCANNER_RESULT_KEY) { _, bundle ->
            bundle.getString(Keys.PRODUCT_BARCODE_KEY)?.let {
                binding.editTextProductsSearch.setText(it)
                viewModel.setQuery(it)
            }
        }
    }

    private fun setupScannerLauncher() {
        scannerLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission())
            { isGranted: Boolean ->
                if (isGranted) {
                    val action = CreateOrderGraphNavDirections.actionGlobalScannerFragment(
                        returnResult = true
                    )
                    findNavController().navigate(action)
                } else UiInterface.showSnackBar(getString(R.string.snack_require_camera_permission))
            }
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
        viewModel.updateOrderTotal()
    }

    private fun onProductAdded(product: Product) {
        viewModel.addProduct(product)
        binding.editTextProductsSearch.setText("")
        binding.editTextProductsSearch.clearFocus()
        hideKeyboard()
    }

    fun navigateToReviewOrder() {
        if (viewModel.areProductsValid()) {
            findNavController().navigate(R.id.reviewOderFragment)
        } else {
            UiInterface.showSnackBar(getString(R.string.product_amount_empty))
        }
    }

    private fun setupToolbar() {
        with(binding.toolbar) {
            registerToolbarBackButton(this)
            setOnMenuItemClickListener {
                if (it.itemId == R.id.action_menu_item_scan) {
                    scannerLauncher.launch(Manifest.permission.CAMERA)
                }
                true
            }
        }
    }

    override fun onDestroyView() {
        binding.recyclerViewOrderProducts.adapter = null
        binding.recyclerViewSearchProducts.adapter = null
        super.onDestroyView()
    }
}
