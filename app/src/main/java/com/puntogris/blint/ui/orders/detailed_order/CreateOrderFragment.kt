package com.puntogris.blint.ui.orders.detailed_order

import android.Manifest
import android.annotation.SuppressLint
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.puntogris.blint.DetailedOrderGraphNavDirections
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentCreateOrderBinding
import com.puntogris.blint.model.product.Product
import com.puntogris.blint.model.product.ProductWithRecord
import com.puntogris.blint.model.order.Record
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.*
import com.puntogris.blint.utils.Constants.IN
import com.puntogris.blint.utils.Constants.PRODUCT_BARCODE_KEY
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CreateOrderFragment :
    BaseFragment<FragmentCreateOrderBinding>(R.layout.fragment_create_order) {

    private val viewModel: NewOrderViewModel by navGraphViewModels(R.id.detailedOrderGraphNav) { defaultViewModelProviderFactory }
    private lateinit var recordsAdapter: CreateRecordsAdapter
    lateinit var scannerLauncher: ActivityResultLauncher<String>

    @SuppressLint("NotifyDataSetChanged")
    override fun initializeViews() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.fragment = this
        binding.viewModel = viewModel
        binding.searchToolbar.setNavigationOnClickListener { findNavController().navigateUp() }

        UiInterface.registerUi(
            showFab = true,
            showAppBar = false,
            showToolbar = false,
            showFabCenter = false,
            fabIcon = R.drawable.ic_baseline_arrow_forward_24
        ) {
            viewModel.updateOrdersItems(recordsAdapter.recordsList)
            viewModel.productWithRecords = recordsAdapter.recordsList
            if (viewModel.productWithRecords.size != 0) {
                if (viewModel.productWithRecords.all {
                        it.record.amount != 0 &&
                                (if (viewModel.order.value?.type == IN) true else it.record.amount <= it.product.amount)
                    }) {
                    findNavController().navigate(R.id.reviewRecordFragment)
                } else {
                    UiInterface.showSnackBar(getString(R.string.product_amount_empty))
                }
            } else {
                UiInterface.showSnackBar(getString(R.string.order_needs_products))
            }
        }

        setUpRecyclerView()
        setupScannerLauncher()

        SearchProductAdapter { onProductAdded(it) }.let {
            binding.productSearchRecyclerView.adapter = it
            subscribeSearchUi(it)
        }

        if (recordsAdapter.currentList.isEmpty()) {
            if (viewModel.productWithRecords.isNotEmpty()) {
                viewModel.productWithRecords.forEach {
                    recordsAdapter.recordsList.add(it)
                }
                recordsAdapter.notifyDataSetChanged()
            }
        }

        binding.productSearchText.addTextChangedListener {
            viewModel.setQuery(it.toString())
        }

        binding.clearTextButton.setOnClickListener {
            binding.productSearchText.setText("")
        }

        onBackStackLiveData<String>(PRODUCT_BARCODE_KEY) {
            lifecycleScope.launch {
                binding.productSearchText.setText(it)
                binding.productSearchRecyclerView.visible()
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

    private fun subscribeSearchUi(adapter: SearchProductAdapter) {
        viewModel.productsLiveData.observe(viewLifecycleOwner) {
            binding.clearTextButton.isVisible = it.isNotEmpty()
            binding.productSearchRecyclerView.isVisible = it.isNotEmpty()
            adapter.submitList(it)
        }
    }

    fun onScanBarcodeButtonClicked() {
        scannerLauncher.launch(Manifest.permission.CAMERA)
    }

    private fun setUpRecyclerView() {
        recordsAdapter = CreateRecordsAdapter(
            requireContext(),
            { onDataChanged() },
            { deleteListener(it) })
        binding.recyclerView.apply {
            adapter = recordsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun deleteListener(productWithRecord: ProductWithRecord) {
        viewModel.productWithRecords.remove(productWithRecord)
        UiInterface.showSnackBar(
            getString(R.string.product_deleted),
            actionText = R.string.action_undo
        ) {
            recordsAdapter.addProductWithRecord(productWithRecord)
            onDataChanged()
        }
    }

    private fun onDataChanged() {
        val newAmount = recordsAdapter.getRecordTotalPrice()
        viewModel.updateOrderValue(newAmount)
        binding.textView155.text = getString(R.string.amount_normal, newAmount.toMoneyFormatted())
    }

    private fun onProductAdded(product: Product) {
        binding.productSearchText.setText("")
        binding.productSearchText.clearFocus()
        if (!viewModel.productWithRecords.any { it.product.productId == product.productId }) {
            val productWithRecord =
                ProductWithRecord(
                    product,
                    Record(
                        productName = product.name,
                        type = viewModel.order.value?.type!!,
                        productId = product.productId,
                        totalInStock = product.historicInStock,
                        totalOutStock = product.historicOutStock
                    )
                )
            recordsAdapter.addProductWithRecord(productWithRecord)
        } else {
            UiInterface.showSnackBar(getString(R.string.product_already_added))
        }
        hideKeyboard()
    }

    override fun onDestroyView() {
        binding.recyclerView.adapter = null
        binding.productSearchRecyclerView.adapter = null
        super.onDestroyView()
    }
}