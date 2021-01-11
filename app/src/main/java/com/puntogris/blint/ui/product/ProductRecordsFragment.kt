package com.puntogris.blint.ui.product

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentProductRecordsBinding
import com.puntogris.blint.model.Product
import com.puntogris.blint.model.Record
import com.puntogris.blint.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProductRecordsFragment : BaseFragment<FragmentProductRecordsBinding>(R.layout.fragment_product_records) {

    private val viewModel: ProductViewModel by viewModels()

    override fun initializeViews() {
        val productRecordsAdapter = ProductRecordsAdapter { onRecordClickListener(it) }
        binding.recyclerView.adapter = productRecordsAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        arguments?.takeIf { it.containsKey("product_key") }?.apply {
            getParcelable<Product>("product_key")?.let { productBundle->
                viewModel.setProductData(productBundle)
                lifecycleScope.launch {
                    viewModel.getProductRecords().collect {
                        productRecordsAdapter.submitData(it)
                    }
                }
            }
        }
    }

    private fun onRecordClickListener(record: Record){

    }

    //    private fun setUpPieView() {
//        binding.tvAmount.text = 5123.toFloat().toUSDFormatted()
//        val rallyPiePortions = listOf(
//            RallyPiePortion("test", 1000F, ContextCompat.getColor(requireContext(), R.color.teal_200)),
//            RallyPiePortion("test", 2000F, ContextCompat.getColor(requireContext(), R.color.teal_700)),
//            RallyPiePortion("test", 1000F, ContextCompat.getColor(requireContext(), R.color.purple_200)),
//            RallyPiePortion("test", 3000F, ContextCompat.getColor(requireContext(), R.color.bottomSheetColorPrimary)),
//        )
//
//        val rallyPieData = RallyPieData(portions = rallyPiePortions, maxValue = 10000F)
//
//        val rallyPieAnimation = RallyPieAnimation(binding.rallyPie)
//        rallyPieAnimation.duration = 600
//        binding.rallyPie.setPieData(pieData = rallyPieData, animation = rallyPieAnimation)
//    }

}