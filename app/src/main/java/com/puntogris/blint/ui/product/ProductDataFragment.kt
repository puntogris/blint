package com.puntogris.blint.ui.product

import android.content.Intent
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import com.nguyenhoanglam.imagepicker.model.Config
import com.nguyenhoanglam.imagepicker.ui.imagepicker.ImagePicker
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentDataProductBinding
import com.puntogris.blint.model.Product
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductDataFragment : BaseFragment<FragmentDataProductBinding>(R.layout.fragment_data_product) {

    private val viewModel: ProductViewModel by viewModels()

    override fun initializeViews() {
        initChips()
        binding.fragment = this
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        arguments?.takeIf { it.containsKey("data_key") }?.apply {
            getParcelable<Product>("data_key")?.let {
                viewModel.setCurrentProductData(it)
            }
        }

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<String>("key")?.observe(
            viewLifecycleOwner) { result ->
            binding.productDescriptionLayout.productBarcodeText.setText(result)
        }

        getParentFab().apply {
            changeIconFromDrawable(R.drawable.ic_baseline_save_24)
            setOnClickListener {
                val name = binding.productDescriptionLayout.productNameText.getString()
                val barcode = binding.productDescriptionLayout.productBarcodeText.getString()
                val description = binding.productDescriptionLayout.productDescriptionText.getString()
                val buyPrice = binding.productPricesLayout.productBuyPriceText.getInt()
                val sellPrice = binding.productPricesLayout.productSellPriceText.getInt()
                val suggestedSellPrice = binding.productPricesLayout.productSuggestedSellPriceText.getInt()
                val product = Product(
                    name = name,
                    barcode = barcode,
                    description = description,
                    buyPrice = buyPrice,
                    sellPrice = sellPrice,
                    suggestedSellPrice = suggestedSellPrice
                )
                when(val validator = StringValidator.from(name, allowSpecialChars = true)){
                    is StringValidator.Valid -> {
                        viewModel.updateCurrentProductData(product)
                        viewModel.saveProduct()
                        createShortSnackBar("Se guardo el producto satisfactoriamente.").setAnchorView(this).show()
                        findNavController().navigateUp()
                    }
                    is StringValidator.NotValid -> createShortSnackBar(validator.error).setAnchorView(this).show()
                }
            }
        }
    }

    fun onSearchButtonClicked(){

    }

    fun onScanButtonClicked(){
        val productFragment = requireParentFragment() as ProductFragment
        productFragment.goToScannerFragment()
    }

    fun onAddImageButtonClicked(){
        ImagePicker.with(this)
            .setFolderMode(true)
            .setFolderTitle("Imagenes")
            .setStatusBarColor(resources.getString(0 + R.color.almostBlack))
            .setMultipleMode(false)
            .setRootDirectoryName(Config.ROOT_DIR_DCIM)
            .setDirectoryName("Blint")
            .setShowNumberIndicator(true)
            .start()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (ImagePicker.shouldHandleResult(requestCode, resultCode, data)) {
            ImagePicker.getImages(data).let {
                if (it.isNotEmpty()) {
                    viewModel.updateProductImage(it.first())
                    binding.productImagesLayout.productImage.visible()
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun onPricesButtonClicked(){
        binding.productPricesButton.toggleIcon()
        binding.productPricesLayout.expandableLayout.toggle()
    }

    fun onDescriptionButtonClicked(){
        binding.productDescriptionButton.toggleIcon()
        binding.productDescriptionLayout.expandableLayout.toggle()
    }

    fun onImageButtonClicked(){
        binding.productImagesLayout.expandableLayout.toggle()
        binding.productImageButton.toggleIcon()
    }

    fun onScopeButtonClicked(){
        binding.productScopeLayout.expandableLayout.toggle()
        binding.productScopeButton.toggleIcon()
    }
    private fun initChips(){
        binding.productScopeLayout.addClientChip.setOnClickListener {
            val newChip = Chip(requireContext())
            newChip.text = "Cliente"
            newChip.isCloseIconVisible = true
            newChip.closeIcon = ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_close_24, null)
            newChip.setOnCloseIconClickListener {
                binding.productScopeLayout.clientsChipGroup.removeView(it)
            }
            binding.productScopeLayout.clientsChipGroup.addView(newChip)
        }
        binding.productScopeLayout.addSupplierChip.setOnClickListener {
            val newChip = Chip(requireContext())
            newChip.text = "Proveedor"
            newChip.isCloseIconVisible = true
            newChip.closeIcon = ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_close_24, null)
            newChip.setOnCloseIconClickListener {
                binding.productScopeLayout.supplierChipGroup.removeView(it)
            }
            binding.productScopeLayout.supplierChipGroup.addView(newChip)
        }
    }
}