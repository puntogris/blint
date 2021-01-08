package com.puntogris.blint.ui.product

import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.flatMap
import androidx.paging.map
import androidx.recyclerview.widget.LinearLayoutManager
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentManageProductsBinding
import com.puntogris.blint.model.Product
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.ui.custom_views.pie_chart.RallyPieAnimation
import com.puntogris.blint.ui.custom_views.pie_chart.RallyPieData
import com.puntogris.blint.ui.custom_views.pie_chart.RallyPiePortion
import com.puntogris.blint.utils.toUSDFormatted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class ManageProductsFragment : BaseFragment<FragmentManageProductsBinding>(R.layout.fragment_manage_products) {

    private val viewModel: ProductViewModel by viewModels()

    override fun initializeViews() {
        val manageProductsAdapter = ManageProductsAdapter { onProductClickListener(it) }
        binding.recyclerView.adapter = manageProductsAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        lifecycleScope.launchWhenStarted {
            viewModel.getAllProducts().collect {
                manageProductsAdapter.submitData(it)
            }
        }
    }

    private fun onProductClickListener(product: Product){
        val action = ManageProductsFragmentDirections.actionManageProductsFragmentToProductFragment(product)
        findNavController().navigate(action)
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


    override fun onDestroyView() {
        binding.recyclerView.adapter = null
        super.onDestroyView()
    }
}