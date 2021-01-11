package com.puntogris.blint.ui.supplier

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentEditSupplierBinding
import com.puntogris.blint.model.Supplier
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditSupplierFragment : BaseFragment<FragmentEditSupplierBinding>(R.layout.fragment_edit_supplier) {

    private val viewModel: SupplierViewModel by viewModels()

    override fun initializeViews() {
        binding.fragment = this
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        arguments?.takeIf { it.containsKey("supplier_key") }?.apply {
            getParcelable<Supplier>("supplier_key")?.let {
                viewModel.setCurrentSupplierData(it)
            }
        }

        getParentFab().apply {
            changeIconFromDrawable(R.drawable.ic_baseline_save_24)
            setOnClickListener {
                viewModel.updateCurrentSupplierData(getSupplierFromViews())
                when(val validator = StringValidator.from(viewModel.currentSupplier.value!!.companyName, allowSpecialChars = true)){
                    is StringValidator.Valid -> {
                        viewModel.saveCurrentSupplierToDatabase()
                        createShortSnackBar("Se guardo el proveedor satisfactoriamente.").setAnchorView(this).show()
                        findNavController().navigateUp()
                    }
                    is StringValidator.NotValid -> createShortSnackBar(validator.error).setAnchorView(this).show()
                }
            }
        }
    }

    private fun getSupplierFromViews(): Supplier {
        return Supplier(
            companyName = binding.companyLayout.supplierCompanyNameText.getString(),
            companyPhone = binding.companyLayout.supplierCompanyPhoneText.getString(),
            address = binding.companyLayout.supplierCompanyAddressText.getString(),
            companyEmail = binding.companyLayout.supplierCompanyEmailText.getString(),
            companyPaymentInfo = binding.supplierExtrasLayout.supplierPaymentInfoText.getString(),
            sellerEmail = binding.sellerLayout.supplierSellerEmailText.getString(),
            sellerName = binding.sellerLayout.supplierSellerNameText.getString(),
            sellerPhone = binding.sellerLayout.supplierSellerPhoneText.getString(),
            notes = binding.supplierExtrasLayout.supplierNotesText.getString()
        )
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