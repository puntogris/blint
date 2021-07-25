package com.puntogris.blint.ui.orders.detailed_order

import android.Manifest
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.puntogris.blint.DetailedOrderGraphNavDirections
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentCreateOrderBinding
import com.puntogris.blint.model.*
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.ui.custom_views.ConstraintRadioGroup
import com.puntogris.blint.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CreateOrderFragment : BaseFragment<FragmentCreateOrderBinding>(R.layout.fragment_create_order) {

    private val viewModel: NewOrderViewModel by navGraphViewModels(R.id.detailedOrderGraphNav) { defaultViewModelProviderFactory }
    private lateinit var recordsAdapter: CreateRecordsAdapter
    lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private var searchJob: Job? = null

    override fun initializeViews() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.fragment = this
        binding.viewModel = viewModel
        binding.searchToolbar.setNavigationOnClickListener { findNavController().navigateUp() }
        setUpRecyclerView()

        registerUiInterface.register(showFab = true, showAppBar = false, showToolbar = false, showFabCenter = false, fabIcon = R.drawable.ic_baseline_arrow_forward_24){
            viewModel.updateOrdersItems(recordsAdapter.recordsList)
            viewModel.productWithRecords = recordsAdapter.recordsList
            if (viewModel.productWithRecords.size != 0){
                if (viewModel.productWithRecords.all {
                        it.record.amount != 0 && it.record.amount <= it.product.amount
                }){
                    searchJob?.cancel()
                    findNavController().navigate(R.id.reviewRecordFragment)
                }else{
                    showSnackBarVisibilityAppBar(getString(R.string.product_amount_empty))
                }
            }else{
                showSnackBarVisibilityAppBar(getString(R.string.order_needs_products))
            }
        }

        val searchAdapter = SearchProductAdapter{ onProductAdded(it) }
        binding.productSearchRecyclerView.adapter = searchAdapter
        binding.productSearchRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        if (recordsAdapter.currentList.isEmpty()){
            if (viewModel.productWithRecords.isNotEmpty()) {
                viewModel.productWithRecords.forEach {
                    recordsAdapter.recordsList.add(it)
                }
                recordsAdapter.notifyDataSetChanged()
            }
        }

        binding.searchTypeRadioGroup.setOnCheckedChangeListener(object : ConstraintRadioGroup.OnCheckedChangeListener{
            override fun onCheckedChanged(group: ConstraintRadioGroup?, checkedId: Int) {
                if (binding.productSearchText.getString().isNotEmpty()){
                    searchJob?.cancel()
                    searchJob = lifecycleScope.launch {
                        val text = binding.productSearchText.getString()
                        val data = when(binding.searchTypeRadioGroup.checkedRadioButtonId){
                            R.id.qrSearchType -> SearchText.QrCode(text)
                            R.id.internalCodeSearchType -> SearchText.InternalCode(text)
                            else -> SearchText.Name(text)
                        }
                        viewModel.getProductWithName(data).collect { pagingData ->
                            binding.productSearchRecyclerView.visible()
                            searchAdapter.submitData(pagingData)
                        }
                    }
                }
            }
        })

        binding.productSearchText.addTextChangedListener {
            searchJob?.cancel()
            it?.toString()?.let{ text ->
                if(text.isNotEmpty()){
                    val data = when(binding.searchTypeRadioGroup.checkedRadioButtonId){
                        R.id.qrSearchType -> SearchText.QrCode(text)
                        R.id.internalCodeSearchType -> SearchText.InternalCode(text)
                        else -> SearchText.Name(text)
                    }
                    searchJob = lifecycleScope.launch {
                        viewModel.getProductWithName(data).collect { pagingData ->
                            binding.productSearchRecyclerView.visible()
                            binding.searchTypeRadioGroup.visible()
                            searchAdapter.submitData(pagingData)
                        }
                    }
                    binding.clearTextButton.visible()
                }else{
                    binding.searchTypeRadioGroup.gone()
                    binding.productSearchRecyclerView.gone()
                    binding.clearTextButton.gone()
                }
            }
        }

        binding.clearTextButton.setOnClickListener {
            binding.productSearchText.setText("")
        }

        onBackStackLiveData<String>("key"){
            lifecycleScope.launch {
                binding.productSearchText.setText(it)
                binding.searchTypeRadioGroup.visible()
                viewModel.getProductWithName(SearchText.QrCode(it)).collect { pagingData ->
                    binding.productSearchRecyclerView.visible()
                    searchAdapter.submitData(pagingData)
                }
            }
        }

        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission())
            { isGranted: Boolean ->
                if (isGranted) {
                    val action = DetailedOrderGraphNavDirections.actionGlobalScannerFragment(1)
                    findNavController().navigate(action)
                }
                else showLongSnackBarAboveFab(getString(R.string.snack_require_camera_permission))
            }
    }

    fun onScanBarcodeButtonClicked(){
        requestPermissionLauncher.launch(Manifest.permission.CAMERA)
    }

    private fun setUpRecyclerView(){
        recordsAdapter = CreateRecordsAdapter(
            requireContext(),
            {onDataChanged()},
            {deleteListener(it)})
        binding.recyclerView.apply {
            adapter = recordsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun deleteListener(productWithRecord: ProductWithRecord){
        createLongSnackBarAboveFab(getString(R.string.product_deleted)).setAction(getString(R.string.action_undo)){
            recordsAdapter.recordsList.add(productWithRecord)
            recordsAdapter.notifyDataSetChanged()
            onDataChanged()
        }.show()
    }

    private fun onDataChanged(){
        val newAmount = recordsAdapter.getRecordTotalPrice()
        viewModel.updateOrderValue(newAmount)
        binding.textView155.text = getString(R.string.amount_normal, newAmount.toMoneyFormatted())
    }

    private fun onProductAdded(product: Product){
        binding.productSearchText.setText("")
        binding.productSearchText.clearFocus()
        binding.searchTypeRadioGroup.gone()
        if (!viewModel.productWithRecords.any { it.product.productId == product.productId }){
            val productWithRecord =
                ProductWithRecord(product,
                    Record(productName = product.name,
                        type = viewModel.order.value?.type!!,
                        productId = product.productId,
                        totalInStock = product.totalInStock,
                        totalOutStock = product.totalOutStock))
            recordsAdapter.recordsList.add(productWithRecord)
            recordsAdapter.notifyDataSetChanged()
        }else{
            showSnackBarVisibilityAppBar(getString(R.string.product_already_added))
        }
        hideKeyboard()
    }

    override fun onDestroyView() {
        binding.recyclerView.adapter = null
        binding.productSearchRecyclerView.adapter = null
        super.onDestroyView()
    }
}