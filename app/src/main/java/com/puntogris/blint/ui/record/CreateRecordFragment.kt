package com.puntogris.blint.ui.record

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentCreateRecordBinding
import com.puntogris.blint.model.Product
import com.puntogris.blint.model.ProductWithRecord
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateRecordFragment : BaseFragment<FragmentCreateRecordBinding>(R.layout.fragment_create_record) {

    private val args: CreateRecordFragmentArgs by navArgs()
    private val viewModel: RecordsViewModel by viewModels()
    private lateinit var recordsAdapter: CreateRecordsAdapter

    override fun initializeViews() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.fragment = this
        binding.viewModel = viewModel

        setUpRecyclerView()

//        lifecycleScope.launchWhenStarted {
//            if (args.productID != 0){
//                lifecycleScope.launch {
//                    val product = viewModel.getProduct(args.productID)
//                    viewModel.setProductData(product)
//                }
//            }
//        }


        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Product>("record_product_selected")?.observe(
            viewLifecycleOwner) {
            onProductAdded(it)
        }

        binding.button19.setOnClickListener {
            findNavController().navigate(R.id.addProductRecordBottomSheet)
        }

        getParentFab().apply {
            changeIconFromDrawable(R.drawable.ic_baseline_save_24)
            setOnClickListener {
            }
        }
    }


    private fun setUpRecyclerView(){
        recordsAdapter = CreateRecordsAdapter(
            requireContext(),
            {onDataChanged(it)},
            {onRecordClicked(it)},
            {deleteListener(it)})
        binding.recyclerView.adapter = recordsAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun onRecordClicked(productWithRecord:ProductWithRecord){

    }

    private fun deleteListener(productWithRecord: ProductWithRecord){
        createLongSnackBarAboveFab("Producto eliminado.").setAction("Deshacer"){
            recordsAdapter.recordsList.add(productWithRecord)
            recordsAdapter.notifyDataSetChanged()
            onDataChanged(recordsAdapter.getRecordTotalPrice())
        }.show()
    }

    private fun onDataChanged(amount:Int){
        binding.textView155.text = amount.toString()
    }

    private fun onProductAdded(product: Product){
        recordsAdapter.recordsList.add(ProductWithRecord(product))
        recordsAdapter.notifyDataSetChanged()
    }
}

