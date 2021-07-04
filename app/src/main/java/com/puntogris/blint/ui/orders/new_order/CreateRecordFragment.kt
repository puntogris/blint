package com.puntogris.blint.ui.orders.new_order

import android.Manifest
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.paging.map
import androidx.recyclerview.widget.LinearLayoutManager
import com.puntogris.blint.NewOrderGraphNavDirections
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentCreateRecordBinding
import com.puntogris.blint.model.*
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.ui.custom_views.ConstraintRadioGroup
import com.puntogris.blint.ui.orders.CreateRecordsAdapter
import com.puntogris.blint.ui.orders.NewOrderViewModel
import com.puntogris.blint.ui.product.EditProductFragmentDirections
import com.puntogris.blint.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CreateRecordFragment : BaseFragment<FragmentCreateRecordBinding>(R.layout.fragment_create_record) {

    private val viewModel: NewOrderViewModel by navGraphViewModels(R.id.newOrderGraphNav) { defaultViewModelProviderFactory }
    private lateinit var recordsAdapter: CreateRecordsAdapter
    lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    override fun initializeViews() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.fragment = this
        binding.viewModel = viewModel
        binding.searchToolbar.setNavigationOnClickListener { findNavController().navigateUp() }

        setUpRecyclerView()

        setUpUi(showFab = true, showAppBar = false, showToolbar = false, showFabCenter = false, fabIcon = R.drawable.ic_baseline_arrow_forward_24){
            viewModel.updateOrdersItems(recordsAdapter.recordsList)
            viewModel.productWithRecords = recordsAdapter.recordsList
            findNavController().navigate(R.id.reviewRecordFragment)
        }

        val searchAdapter = SearchProductAdapter{ onProductAdded(it) }
        binding.productSearchRecyclerView.adapter = searchAdapter
        binding.productSearchRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        if (recordsAdapter.currentList.isEmpty()){
            if (viewModel.productWithRecords.isNotEmpty()) {
                viewModel.productWithRecords.forEach {
                    recordsAdapter.recordsList.add(it)
                }
                recordsAdapter.submitList(viewModel.productWithRecords)
                recordsAdapter.notifyDataSetChanged()
            }
        }

        binding.searchTypeRadioGroup.setOnCheckedChangeListener(object : ConstraintRadioGroup.OnCheckedChangeListener{
            override fun onCheckedChanged(group: ConstraintRadioGroup?, checkedId: Int) {
                if (binding.productSearchText.getString().isNotEmpty()){
                    lifecycleScope.launch {
                        val text = binding.productSearchText.getString()
                        val data = when(binding.searchTypeRadioGroup.checkedRadioButtonId){
                            R.id.qrSearchType -> SearchText.QrCode(text)
                            R.id.internalCodeSearchType -> SearchText.InternalCode(text)
                            else -> SearchText.Name(text)
                        }
                        viewModel.getProductWithName(data).collect { pagingData ->
                            binding.productSearchRecyclerView.visible()
                            searchAdapter.submitData(pagingData.map { product->
                                product.product
                            })
                        }
                    }
                }
            }
        })

        binding.productSearchText.addTextChangedListener {
            it?.toString()?.let{ text ->
                if(text.isNotEmpty()){
                    val data = when(binding.searchTypeRadioGroup.checkedRadioButtonId){
                        R.id.qrSearchType -> SearchText.QrCode(text)
                        R.id.internalCodeSearchType -> SearchText.InternalCode(text)
                        else -> SearchText.Name(text)
                    }
                    lifecycleScope.launch {
                        viewModel.getProductWithName(data).collect { pagingData ->
                            binding.productSearchRecyclerView.visible()
                            binding.searchTypeRadioGroup.visible()
                            searchAdapter.submitData(pagingData.map { product->
                                product.product
                            })
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

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<String>("key")?.observe(
            viewLifecycleOwner) {
            lifecycleScope.launch {
                binding.productSearchText.setText(it)
                binding.searchTypeRadioGroup.visible()
                viewModel.getProductWithName(SearchText.QrCode(it)).collect { pagingData ->
                    binding.productSearchRecyclerView.visible()
                    searchAdapter.submitData(pagingData.map { product->
                        product.product
                    })
                }
            }
        }

        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission())
            { isGranted: Boolean ->
                if (isGranted) {
                    val action = NewOrderGraphNavDirections.actionGlobalScannerFragment(1)
                    findNavController().navigate(action)
                }
                else showLongSnackBarAboveFab("Necesitamos acceso a la camara para poder abrir el escaner.")
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
        binding.recyclerView.adapter = recordsAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun deleteListener(productWithRecord: ProductWithRecord){
        createLongSnackBarAboveFab("Producto eliminado.").setAction("Deshacer"){
            recordsAdapter.recordsList.add(productWithRecord)
            recordsAdapter.notifyDataSetChanged()
            onDataChanged()
        }.show()
    }

    private fun onDataChanged(){
        val newAmount = recordsAdapter.getRecordTotalPrice()
        viewModel.updateOrderValue(newAmount)
        binding.textView155.text = newAmount.toString()
    }

    private fun onProductAdded(product: Product){
        binding.productSearchText.setText("")
        binding.productSearchText.clearFocus()
        binding.searchTypeRadioGroup.gone()
        hideKeyboard()
        val productWithRecord =
            ProductWithRecord(product, Record(productName = product.name, productId = product.productId))
        recordsAdapter.recordsList.add(productWithRecord)
        recordsAdapter.notifyDataSetChanged()
    }
}

