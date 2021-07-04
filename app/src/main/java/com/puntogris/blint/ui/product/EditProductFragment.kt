package com.puntogris.blint.ui.product

import android.Manifest
import android.content.Intent
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.chip.Chip
import com.maxkeppeler.sheets.options.DisplayMode
import com.maxkeppeler.sheets.options.Option
import com.maxkeppeler.sheets.options.OptionsSheet
import com.nguyenhoanglam.imagepicker.model.Config
import com.nguyenhoanglam.imagepicker.ui.imagepicker.ImagePicker
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentEditProductBinding
import com.puntogris.blint.model.Product
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class EditProductFragment : BaseFragment<FragmentEditProductBinding>(R.layout.fragment_edit_product) {

    private val viewModel: ProductViewModel by viewModels()
    private val args: EditProductFragmentArgs by navArgs()
    lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    override fun initializeViews() {
        binding.fragment = this
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        setUpUi(showFab = true, fabIcon = R.drawable.ic_baseline_save_24){
            viewModel.updateProductData(getProductDataFromViews())
            when(val validator = StringValidator.from(viewModel.currentProduct.value!!.name, allowSpecialChars = true)){
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
                    viewModel.setProductData(it.product)

                    it.suppliers?.forEach { supplier ->
                        Chip(requireContext()).apply {
                            text = supplier.companyName
                            setOnClickListener {

                            }
                            binding.scopeLayout.supplierChipGroup.addView(this)
                        }
                    }

                    it.categories?.forEach { category ->
                        Chip(requireContext()).apply {
                            text = category.name
                            setOnClickListener {

                            }
                            binding.productExtrasLayout.categoriesChipGroup.addView(this)
                        }
                    }
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
                productPricesTitle.text = getString(R.string.prices)
            }
        }

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<String>("key")?.observe(
            viewLifecycleOwner) {
            binding.descriptionLayout.productBarcodeText.setText(it)
        }


        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission())
            { isGranted: Boolean ->
                if (isGranted) {
                    val action = EditProductFragmentDirections.actionEditProductFragmentToScannerFragment(1)
                    findNavController().navigate(action)
                }
                else showLongSnackBarAboveFab(getString(R.string.snack_require_camera_permition))
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
        ImagePicker.with(this)
            .setFolderMode(true)
            .setFolderTitle(getString(R.string.image_picker_folder_title))
            .setStatusBarColor(resources.getString(0 + R.color.almostBlack))
            .setMultipleMode(false)
            .setRootDirectoryName(Config.ROOT_DIR_DCIM)
            .setDirectoryName(getString(R.string.app_name))
            .setShowNumberIndicator(true)
            .start()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (ImagePicker.shouldHandleResult(requestCode, resultCode, data)) {
            ImagePicker.getImages(data).let {
                if (it.isNotEmpty()) {
                    val imageMap = hashMapOf("uri" to it.first().uri.toString(), "path" to it.first().path)
                    viewModel.updateProductImage(imageMap)
                    binding.imagesLayout.productImage.visible()
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
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

    fun openBottomSheetForSuppliers(){
        lifecycleScope.launch {
            val suppliers = viewModel.getAllSuppliers()
            if (suppliers.isNullOrEmpty()){
                showLongSnackBarAboveFab("No se encontraron proveedores registrados.")
            }else{
                val optionSuppliers = suppliers.map { Option(it.companyName) }.toMutableList()
                OptionsSheet().build(requireContext()){
                    title(getString(R.string.add_suppliers))
                    displayMode(DisplayMode.LIST)
                    multipleChoices(true)
                    with(optionSuppliers)
                    onPositiveMultiple(getString(R.string.action_add)) { selectedIndices: MutableList<Int>, _ ->
                        viewModel.updateSuppliers(selectedIndices.map { suppliers[it].supplierId })
                        selectedIndices.forEach {
                            createNewChipAndAddItToGroup(suppliers[it].companyName, binding.scopeLayout.supplierChipGroup)
                        }
                    }
                    onNegative(getString(R.string.action_cancel))
                }.show(parentFragmentManager, "")
            }
        }
    }

    fun openBottomSheetForCategories(){
        lifecycleScope.launch {
            val categories = viewModel.getAllCategories()
            if (categories.isNullOrEmpty()){
                showLongSnackBarAboveFab("No se encontraron categorias registrados.")
            }else{
                val optionSuppliers = categories.map { Option(it.name) }.toMutableList()
                OptionsSheet().build(requireContext()){
                    title(getString(R.string.add_categories))
                    displayMode(DisplayMode.LIST)
                    multipleChoices(true)
                    with(optionSuppliers)
                    onPositiveMultiple(getString(R.string.action_add)) { selectedIndices: MutableList<Int>, _ ->
                        viewModel.updateCategories(selectedIndices.map { categories[it].categoryId })
                        selectedIndices.forEach {
                            createNewChipAndAddItToGroup(categories[it].name, binding.scopeLayout.supplierChipGroup)
                        }
                    }
                    onNegative(getString(R.string.action_cancel))
                }.show(parentFragmentManager, "")
            }
        }
    }
}