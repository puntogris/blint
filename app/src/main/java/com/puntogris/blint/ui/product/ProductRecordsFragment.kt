package com.puntogris.blint.ui.product

import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentProductRecordsBinding
import com.puntogris.blint.model.Record
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.ui.custom_views.pie_chart.PieChartAnimation
import com.puntogris.blint.ui.custom_views.pie_chart.RallyPieData
import com.puntogris.blint.ui.custom_views.pie_chart.RallyPiePortion
import com.puntogris.blint.ui.record.ProductsRecordsAdapter
import com.puntogris.blint.utils.toUSDFormatted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProductRecordsFragment : BaseFragment<FragmentProductRecordsBinding>(R.layout.fragment_product_records) {

    private val viewModel: ProductViewModel by viewModels()

    override fun initializeViews() {
        setUpPieView()
        val productsRecordsAdapter = ProductsRecordsAdapter { onRecordClickListener(it) }
        binding.recyclerView.adapter = productsRecordsAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        arguments?.takeIf { it.containsKey("product_key") }?.apply {
            getInt("product_key").let { productID->
                lifecycleScope.launch {
                    viewModel.getProductRecords(productID).collect {
                        productsRecordsAdapter.submitData(it)
                    }
                }
            }
        }

    }

    private fun setUpPieView() {
        binding.productAmountNetWorth.text = 5004.13.toFloat().toUSDFormatted()
        val rallyPiePortions = listOf(
            RallyPiePortion("it.name", 3500F, ContextCompat.getColor(requireContext(), R.color.teal_200))
        )

        val rallyPieData =  RallyPieData(portions = rallyPiePortions, maxValue = 5000F)
        val rallyPieAnimation = PieChartAnimation(binding.pieChart)
        rallyPieAnimation.duration = 600
        binding.pieChart.setPieData(pieData = rallyPieData, animation = rallyPieAnimation)
    }

    private fun onRecordClickListener(record: Record){

    }

}