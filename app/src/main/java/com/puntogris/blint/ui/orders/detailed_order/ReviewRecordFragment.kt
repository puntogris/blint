package com.puntogris.blint.ui.orders.detailed_order

import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentReviewRecordBinding
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.ui.orders.NewOrderViewModel
import com.puntogris.blint.utils.getDateWithTimeFormattedString
import com.puntogris.blint.utils.setUpUi
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class ReviewRecordFragment: BaseFragment<FragmentReviewRecordBinding>(R.layout.fragment_review_record) {

    private val viewModel: NewOrderViewModel by navGraphViewModels(R.id.detailedOrderGraphNav) { defaultViewModelProviderFactory }

    override fun initializeViews() {
        setUpUi(showFab = true, showAppBar = false, showFabCenter = false, fabIcon = R.drawable.ic_baseline_arrow_forward_24){
            findNavController().navigate(R.id.publishOrderFragment)
        }

        viewModel.refreshOrderValue()
        binding.textView168.text = viewModel.getOrder().items.size.toString()
        binding.textView166.text = viewModel.getOrder().items.sumByDouble { it.value.toDouble() }.toString()
        binding.textView175.text = viewModel.getCurrentUserEmail()
        binding.textView171.text = Date().getDateWithTimeFormattedString()
        binding.textView178.text = if(viewModel.getOrder().traderName.isNotEmpty()) viewModel.getOrder().traderName else "No especificado"

    }

}