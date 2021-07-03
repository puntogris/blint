package com.puntogris.blint.ui.orders.new_order

import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.paging.map
import androidx.recyclerview.widget.LinearLayoutManager
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentCreateRecordBinding
import com.puntogris.blint.model.*
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.ui.orders.CreateRecordsAdapter
import com.puntogris.blint.ui.orders.NewOrderViewModel
import com.puntogris.blint.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CreateRecordFragment : BaseFragment<FragmentCreateRecordBinding>(R.layout.fragment_create_record) {

    private val viewModel: NewOrderViewModel by navGraphViewModels(R.id.newOrderGraphNav) { defaultViewModelProviderFactory }
    private lateinit var recordsAdapter: CreateRecordsAdapter

    override fun initializeViews() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.fragment = this
        binding.viewModel = viewModel
        binding.searchToolbar.setNavigationOnClickListener { findNavController().navigateUp() }

        setUpRecyclerView()

        if (recordsAdapter.currentList.isEmpty()){
            if (viewModel.productWithRecords.isNotEmpty()) {
                viewModel.productWithRecords.forEach {
                    recordsAdapter.recordsList.add(it)
                }
                recordsAdapter.submitList(viewModel.productWithRecords)
                recordsAdapter.notifyDataSetChanged()
            }
        }

        setUpUi(showFab = true, showAppBar = false, showToolbar = false, showFabCenter = false, fabIcon = R.drawable.ic_baseline_arrow_forward_24){
            viewModel.updateOrdersItems(recordsAdapter.recordsList)
            viewModel.productWithRecords = recordsAdapter.recordsList
            findNavController().navigate(R.id.action_createRecordFragment_to_reviewRecordFragment)
        }

        val searchAdapter = SearchProductAdapter{ onProductAdded(it) }
        binding.productSearchRecyclerView.adapter = searchAdapter
        binding.productSearchRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        binding.productSearchText.addTextChangedListener {
            it?.toString()?.let{ text ->
                lifecycleScope.launch {
                    viewModel.getProductWithName(text).collect { pagingData ->
                        binding.productSearchRecyclerView.visible()
                        searchAdapter.submitData(pagingData.map { product->
                            product.product
                        })
                    }
                }
                if (text.isNotEmpty()) binding.clearTextButton.visible()
                else binding.clearTextButton.gone()
            }
        }
        binding.clearTextButton.setOnClickListener {
            binding.productSearchText.setText("")
        }
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
        hideKeyboard()
        val productWithRecord =
            ProductWithRecord(product, Record(productName = product.name, productId = product.productId))
        recordsAdapter.recordsList.add(productWithRecord)
        recordsAdapter.notifyDataSetChanged()
    }
}

