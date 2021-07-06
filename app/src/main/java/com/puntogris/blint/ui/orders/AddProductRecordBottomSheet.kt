package com.puntogris.blint.ui.orders

import android.view.View
import android.view.WindowManager
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.puntogris.blint.R
import com.puntogris.blint.databinding.AddProductRecordBottomSheetBinding
import com.puntogris.blint.model.Product
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.hideKeyboard
import com.puntogris.blint.utils.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddProductRecordBottomSheet: BaseFragment<AddProductRecordBottomSheetBinding>(R.layout.add_product_record_bottom_sheet){

    private val viewModel: NewOrderViewModel by navGraphViewModels(R.id.detailedOrderGraphNav) { defaultViewModelProviderFactory }

//    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//        val bottomSheetDialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
//
//        bottomSheetDialog.setOnShowListener {
//            val bottomSheet =
//                bottomSheetDialog.findViewById<FrameLayout>(
//                    com.google.android.material.R.id.design_bottom_sheet
//                )
//            bottomSheet?.let {
//                setupFullHeight(it)
//            }
//            val behavior = BottomSheetBehavior.from(bottomSheet!!)
//            behavior.state = BottomSheetBehavior.STATE_EXPANDED
//
//        }
//        return bottomSheetDialog
//    }

    override fun initializeViews() {

        var products = emptyList<Product>()

        binding.productSearchText.apply {
            addTextChangedListener {
//                lifecycleScope.launch {
//                    products = viewModel.getProductWithName("%${it.toString()}%")
//                    val productNames = products.map { it.name }
//                    val adapter = ArrayAdapter(requireContext(),R.layout.dropdown_item_list,productNames)
//                    binding.productSearchText.setAdapter(adapter)
//                }
            }
            setOnItemClickListener { _, _, i, _ ->
                val product = products[i]
                hideKeyboard()
                binding.foundProductGroup.visible()
                binding.productName.text = product.name
                binding.productPrice.text = product.buyPrice.toString()
                binding.productStock.text = product.amount.toString()

                binding.button18.setOnClickListener {
                    findNavController().apply {
                        previousBackStackEntry!!.savedStateHandle.set("record_product_selected", product)
                        popBackStack()
                    }
                }

            }
        }
        binding.imageView8.setOnClickListener {
            findNavController().navigate(R.id.scannerFragment)
        }


    }

    private fun setupFullHeight(bottomSheet: View) {
        val layoutParams = bottomSheet.layoutParams
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
        bottomSheet.layoutParams = layoutParams
    }

}