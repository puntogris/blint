package com.puntogris.blint.ui.orders.detailed_order

import android.widget.ArrayAdapter
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentReviewRecordBinding
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.*
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class ReviewRecordFragment: BaseFragment<FragmentReviewRecordBinding>(R.layout.fragment_review_record) {

    private val viewModel: NewOrderViewModel by navGraphViewModels(R.id.detailedOrderGraphNav) { defaultViewModelProviderFactory }

    override fun initializeViews() {
        setUpUi(showFab = true, showAppBar = false, showFabCenter = false, fabIcon = R.drawable.ic_baseline_arrow_forward_24){
            findNavController().navigate(R.id.publishOrderFragment)
        }

        binding.debtText.setAdapter(ArrayAdapter(requireContext(),R.layout.dropdown_item_list, listOf("Completado", "Deuda")))

        binding.debtText.setOnItemClickListener { _, _, i, _ ->
            when(i){
                0 -> {
                    binding.debtAmount.gone()
                    binding.debtSummary.text = "Se pago $${viewModel.getOrder().value} del total de $${viewModel.getOrder().value}"
                }
                1 -> {
                   // if (viewModel.getOrder().traderId.isNotEmpty()){
                        binding.debtAmount.visible()
                        binding.debtSummary.text = "Se pago $${viewModel.getOrder().value} del total de $${viewModel.getOrder().value}"
                   // }else{
                   //     binding.debtText.setText("Completado")
                   //     showShortSnackBar("Se necesita una proveedor/ cliente en la order para generar una deuda.")
               //     }

                }
            }
            viewModel.updateRecordType(i)
        }

        binding.debtAmountText.addTextChangedListener{
            val debt = viewModel.getOrder().value - it.toString().toFloat()
            binding.debtSummary.text = "Se pago $$debt del total de $${viewModel.getOrder().value}"
        }

        viewModel.refreshOrderValue()
        binding.textView168.text = viewModel.getOrder().items.size.toString()
        binding.textView166.text = viewModel.getOrder().items.sumByDouble { it.value.toDouble() }.toString()
        binding.textView175.text = viewModel.getCurrentUserEmail()
        binding.textView171.text = Date().getDateWithTimeFormattedString()
        binding.textView178.text = if(viewModel.getOrder().traderName.isNotEmpty()) viewModel.getOrder().traderName else "No especificado"

    }

    override fun onDestroyView() {
        binding.debtText.setAdapter(null)
        super.onDestroyView()
    }

}