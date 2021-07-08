package com.puntogris.blint.ui.product

import android.Manifest
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.size
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.chip.Chip
import com.maxkeppeler.sheets.options.DisplayMode
import com.maxkeppeler.sheets.options.Option
import com.maxkeppeler.sheets.options.OptionsSheet
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentEditProductBinding
import com.puntogris.blint.model.*
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditProductFragment : BaseFragment<FragmentEditProductBinding>(R.layout.fragment_edit_product) {

    private val viewModel: ProductViewModel by viewModels()
    private val args: EditProductFragmentArgs by navArgs()
    lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var getContent: ActivityResultLauncher<String>

    override fun initializeViews() {
        binding.fragment = this
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                viewModel.updateProductImage(uri.toString())
                binding.descriptionLayout.productImage.visible()
            }
        }

        setUpUi(showFab = true, fabIcon = R.drawable.ic_baseline_save_24){
            viewModel.updateProductData(getProductDataFromViews())
            when(val validator = StringValidator.from(viewModel.currentProduct.value!!.product.name, allowSpecialChars = true)){
                is StringValidator.Valid -> {
                    lifecycleScope.launch {
                        when(viewModel.saveProductDatabase()){
                            SimpleResult.Failure ->
                                createShortSnackBar(getString(R.string.snack_create_product_success)).setAnchorView(it).show()
                            SimpleResult.Success -> {
                                createShortSnackBar(getString(R.string.snack_create_product_error)).setAnchorView(it).show()
                                findNavController().navigateUp()
                            }
                        }
                    }
                }
                is StringValidator.NotValid -> createShortSnackBar(validator.error).setAnchorView(it).show()
            }
        }

        if (!viewModel.viewsLoaded) {
            args.productWithSuppCate?.let{
                lifecycleScope.launch {
                    viewModel.setProductData(it)
                }
            }
            args.barcodeScanned.let { if (it.isNotBlank()) viewModel.updateCurrentProductBarcode(it) }
            viewModel.viewsLoaded = true
        }

        if (args.productWithSuppCate == null) {
            binding.pricesLayout.apply {
                productAmount.visible()
                increaseAmountButton.visible()
                decreaseAmountButton.visible()
                productPricesTitle.text = getString(R.string.prices_and_initial_stock)
            }
        }

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<String>("key")?.observe(
            viewLifecycleOwner) {
            binding.descriptionLayout.productBarcodeText.setText(it)
        }

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<List<FirestoreCategory>>("categories_key")?.observe(
            viewLifecycleOwner) {
            viewModel.updateCategories(it)
            binding.productExtrasLayout.categoriesChipGroup.let { group->
                group.removeViews(1,group.size - 1)
            }
            it.forEach { category ->
               if (!viewModel.currentProduct.value?.categories.isNullOrEmpty()){
                   if (viewModel.currentProduct.value?.categories!!.contains(category)){
                       val chip = Chip(requireContext())
                       chip.text = category.name
                       binding.productExtrasLayout.categoriesChipGroup.addView(chip)
                   }
               }
            }
        }

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<List<FirestoreSupplier>>("suppliers_key")?.observe(
            viewLifecycleOwner) {
            viewModel.updateSuppliers(it)
            binding.scopeLayout.supplierChipGroup.let { group->
                group.removeViews(1,group.size - 1)
            }
            it.forEach { supplier ->
                if (!viewModel.currentProduct.value?.suppliers.isNullOrEmpty()){
                    if (viewModel.currentProduct.value?.suppliers!!.contains(supplier)){
                        val chip = Chip(requireContext())
                        chip.text = supplier.companyName
                        binding.productExtrasLayout.categoriesChipGroup.addView(chip)
                    }
                }
            }
        }

        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission())
            { isGranted: Boolean ->
                if (isGranted) {
                    val action = EditProductFragmentDirections.actionEditProductFragmentToScannerFragment(1)
                    findNavController().navigate(action)
                }
                else showLongSnackBarAboveFab(getString(R.string.snack_require_camera_permission))
            }
    }

    private fun getProductDataFromViews(): Product {
        return Product(
            name = binding.descriptionLayout.productNameText.getString(),
            barcode = binding.descriptionLayout.productBarcodeText.getString(),
            description = binding.descriptionLayout.productDescriptionText.getString(),
            buyPrice = binding.pricesLayout.productBuyPriceText.getFloat(),
            sellPrice = binding.pricesLayout.productSellPriceText.getFloat(),
            suggestedSellPrice = binding.pricesLayout.productSuggestedSellPriceText.getFloat(),
            amount = binding.pricesLayout.productAmountText.getInt(),
            image = viewModel.productImage.value!!,
            internalCode = binding.productExtrasLayout.productInternalCodeText.getString(),
            brand = binding.productExtrasLayout.productBrandText.getString(),
            size = binding.productExtrasLayout.productSizeText.getString())
    }

    fun onScanButtonClicked(){
        viewModel.updateProductData(getProductDataFromViews())
        requestPermissionLauncher.launch(Manifest.permission.CAMERA)
    }

    fun onAddImageButtonClicked(){
        viewModel.updateProductData(getProductDataFromViews())
        getContent.launch("image/*")
    }

    fun onRemoveImageButtonClicked(){
        viewModel.removeCurrentImage()
    }

    fun onIncreaseAmountButtonClicked(){
        binding.pricesLayout.productAmountText.apply {
            setText(getInt().inc().toString())
        }
    }
    fun onDecreaseAmountButtonClicked(){
        binding.pricesLayout.productAmountText.apply {
            setText(getInt().dec().toString())
        }
    }

    fun navigateToProductSuppliers(){
        viewModel.updateProductData(getProductDataFromViews())
        val action = EditProductFragmentDirections
            .actionEditProductFragmentToProductSupplierFragment(viewModel.currentProduct.value?.suppliers?.toTypedArray())
        findNavController().navigate(action)
    }

    fun navigateToProductCategories(){
        viewModel.updateProductData(getProductDataFromViews())
        val action = EditProductFragmentDirections
            .actionEditProductFragmentToProductCategoryFragment(viewModel.currentProduct.value?.categories?.toTypedArray())
        findNavController().navigate(action)
    }

}