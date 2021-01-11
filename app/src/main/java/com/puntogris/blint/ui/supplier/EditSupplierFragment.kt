package com.puntogris.blint.ui.supplier

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentEditSupplierBinding
import com.puntogris.blint.model.Supplier
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.ui.product.EditProductFragmentArgs
import com.puntogris.blint.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditSupplierFragment : BaseFragment<FragmentEditSupplierBinding>(R.layout.fragment_edit_supplier) {

    private val viewModel: SupplierViewModel by viewModels()
    private val args: EditSupplierFragmentArgs by navArgs()

    override fun initializeViews() {
        binding.fragment = this
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        args.supplier?.let {
            viewModel.setSupplierData(it)
        }

        getParentFab().apply {
            changeIconFromDrawable(R.drawable.ic_baseline_save_24)
            setOnClickListener {
                viewModel.updateSupplierData(getSupplierFromViews())
                when(val validator = StringValidator.from(viewModel.currentSupplier.value!!.companyName, allowSpecialChars = true)){
                    is StringValidator.Valid -> {
                        viewModel.saveSupplierDatabase()
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
            supplierCompanyExpandableLayout.toggle()
            supplierCompanyButton.toggleIcon()
        }
        hideKeyboard()
    }

    fun onSellerButtonClicked(){
        binding.sellerLayout.apply {
            supplierSellerExpandableLayout.toggle()
            supplierSellerButton.toggleIcon()
        }
        hideKeyboard()
    }

    fun onExtraButtonClicked(){
        binding.supplierExtrasLayout.apply {
            supplierExtrasExpandableLayout.toggle()
            supplierExtrasButton.toggleIcon()
        }
        hideKeyboard()
    }
}