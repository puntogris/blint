package com.puntogris.blint.ui.product

import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentProductRecordsBinding
import com.puntogris.blint.model.Product
import com.puntogris.blint.model.ProductWithSuppliersCategories
import com.puntogris.blint.model.Record
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.ui.custom_views.pie_chart.PieChartAnimation
import com.puntogris.blint.ui.custom_views.pie_chart.RallyPieData
import com.puntogris.blint.ui.custom_views.pie_chart.RallyPiePortion
import com.puntogris.blint.ui.orders.manage.RecordsAdapter
import com.puntogris.blint.utils.registerUiInterface
import com.puntogris.blint.utils.showOrderPickerAndNavigate
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProductRecordsFragment : BaseFragment<FragmentProductRecordsBinding>(R.layout.fragment_product_records) {

    private val viewModel: ProductViewModel by viewModels(ownerProducer = {requireParentFragment()} )

    override fun initializeViews() {
        registerUiInterface.register(showFab = true){
            showOrderPickerAndNavigate()
        }

        val productsRecordsAdapter = RecordsAdapter { onRecordClickListener(it) }
        binding.recyclerView.adapter = productsRecordsAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        arguments?.takeIf { it.containsKey("product_key") }?.apply {
            getParcelable<ProductWithSuppliersCategories>("product_key")?.let { product ->
                lifecycleScope.launch {
                    viewModel.getProductRecords(product.product.productId).collect {
                        productsRecordsAdapter.submitData(it)
                    }
                }
                setUpPieView(product.product)
            }
        }
    }

    fun onCreateDetailedOrderClicked(){
        findNavController().navigate(R.id.detailedOrderGraphNav)
    }

    private fun setUpPieView(product:Product) {
        binding.productAmountNetWorth.text = (product.amount * product.buyPrice).toString()
        val rallyPiePortions = listOf(
            RallyPiePortion(product.name, product.totalOutStock.toFloat(), ContextCompat.getColor(requireContext(), R.color.teal_200)),
            RallyPiePortion(product.name, product.totalInStock.toFloat(), ContextCompat.getColor(requireContext(), R.color.purple_200))

        )
        val rallyPieData =  RallyPieData(portions = rallyPiePortions, maxValue = product.totalInStock.toFloat() + product.totalOutStock + product.amount)
        val rallyPieAnimation = PieChartAnimation(binding.pieChart)
        rallyPieAnimation.duration = 600
        binding.pieChart.setPieData(pieData = rallyPieData, animation = rallyPieAnimation)
    }

    private fun onRecordClickListener(record: Record){
        (requireParentFragment() as ProductFragment).navigateToInfoRecord(record)
    }
}