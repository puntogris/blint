package com.puntogris.blint.ui.supplier

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentSupplierDataBinding
import com.puntogris.blint.model.Product
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SupplierDataFragment : BaseFragment<FragmentSupplierDataBinding>(R.layout.fragment_supplier_data) {

    private val viewModel: SupplierViewModel by viewModels()

    override fun initializeViews() {
        binding.fragment = this
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        arguments?.takeIf { it.containsKey("data_key") }?.apply {
            getParcelable<Product>("data_key")?.let { productBundle->
                viewModel.currentSupplier.value.let { savedProduct ->
                   // binding.productDeletionButton.visible()
                    if (productBundle.id != savedProduct?.id){
                    //    viewModel.setCurrentProductData(productBundle)
                   //     viewModel.updateProductImage(productBundle.image)
                    }
                }
            }
        }

        getParentFab().apply {
            changeIconFromDrawable(R.drawable.ic_baseline_save_24)
            setOnClickListener {
                setOnClickListener {
                  //  viewModel.updateCurrentProductData(getProductDataFromViews())
                    when(val validator = StringValidator.from(viewModel.currentSupplier.value!!.companyName, allowSpecialChars = true)){
                        is StringValidator.Valid -> {
                            viewModel.saveCurrentSupplierToDatabase()
                            createShortSnackBar("Se guardo el producto satisfactoriamente.").setAnchorView(this).show()
                            findNavController().navigateUp()
                        }
                        is StringValidator.NotValid -> createShortSnackBar(validator.error).setAnchorView(this).show()
                    }
                }
            }
        }
    }


    fun onCompanyButtonClicked(){
        binding.companyLayout.apply {
            expandableLayout.toggle()
            supplierCompanyButton.toggleIcon()
        }
        hideKeyboard()
    }

    fun onSellerButtonClicked(){
        binding.sellerLayout.apply {
            expandableLayout.toggle()
            supplierSellerButton.toggleIcon()
        }
        hideKeyboard()
    }

    fun onExtraButtonClicked(){
        binding.supplierExtrasLayout.apply {
            expandableLayout.toggle()
            supplierExtrasButton.toggleIcon()
        }
        hideKeyboard()
    }

}