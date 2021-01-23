package com.puntogris.blint.ui.product

import android.Manifest
import android.content.Intent
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
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditProductFragment : BaseFragment<FragmentEditProductBinding>(R.layout.fragment_edit_product) {

    private val viewModel: ProductViewModel by viewModels()
    private val args: EditProductFragmentArgs by navArgs()
    lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    override fun initializeViews() {
        binding.fragment = this
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        if (!viewModel.viewsLoaded) {
            if (args.productID != 0){
                lifecycleScope.launch {
                    val productWithSuppliers = viewModel.getProductWithSuppliers(args.productID)
                    viewModel.setProductData(productWithSuppliers.product)

                    if (productWithSuppliers.suppliers.isNotEmpty()){
                        productWithSuppliers.suppliers.forEach { supplier->
                            Chip(requireContext()).apply {
                                text = supplier.companyName
                                setOnClickListener {

                                }
                                binding.scopeLayout.supplierChipGroup.addView(this)
                            }
                        }
                    }
                }

            }
            args.barcodeScanned.let { if (it.isNotBlank()) viewModel.updateCurrentProductBarcode(it) }
            viewModel.viewsLoaded = true
        }

        if (args.productID == 0) {
            binding.pricesLayout.apply {
                productAmount.visible()
                increaseAmountButton.visible()
                decreaseAmountButton.visible()
                productPricesTitle.text = "Precios"
            }
        }

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<String>("key")?.observe(
            viewLifecycleOwner) {
            binding.descriptionLayout.productBarcodeText.setText(it)
        }

        getParentFab().apply {
            changeIconFromDrawable(R.drawable.ic_baseline_save_24)
            setOnClickListener {
                viewModel.updateProductData(getProductDataFromViews())
                when(val validator = StringValidator.from(viewModel.currentProduct.value!!.name, allowSpecialChars = true)){
                    is StringValidator.Valid -> {
                        lifecycleScope.launch {
                            viewModel.saveProductDatabase()
                            createShortSnackBar("Se guardo el producto satisfactoriamente.").setAnchorView(this@apply).show()
                            findNavController().navigateUp()
                        }
                    }
                    is StringValidator.NotValid -> createShortSnackBar(validator.error).setAnchorView(this).show()
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
                else showLongSnackBarAboveFab("Necesitamos acceso a la camara para poder abrir el escaner.")
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
            size = binding.productExtrasLayout.productSizeText.getString(),
            suppliers = viewModel.suppliers.value!!
        )
    }

    fun onSearchButtonClicked(){

    }

    fun onScanButtonClicked(){
        viewModel.updateProductData(getProductDataFromViews())
        requestPermissionLauncher.launch(Manifest.permission.CAMERA)
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
                    title("Agregar Proveedores")
                    displayMode(DisplayMode.LIST)
                    multipleChoices(true)
                    with(optionSuppliers)
                    onPositiveMultiple("Agregar") { selectedIndices: MutableList<Int>, _ ->
                        viewModel.updateSuppliers(selectedIndices.map { suppliers[it].supplierId })
                        selectedIndices.forEach {
                            createNewChipAndAddItToGroup(suppliers[it].companyName, binding.scopeLayout.supplierChipGroup)
                        }
                    }
                    onNegative("Cancelar")
                }.show(parentFragmentManager, "")
            }
        }
    }

    fun openBottomSheetForCategories(){
        OptionsSheet().build(requireContext()){
            title("Agregar Categorias")
            displayMode(DisplayMode.LIST)
            multipleChoices()
            with(
                Option("Categoria 1"),
                Option("Categoria 2"),
                Option("Categoria 3"),
                Option("Categoria 4"),
                Option("Categoria 5"),
                Option("Categoria 6")
            )
            onPositiveMultiple("Agregar") { selectedIndices: MutableList<Int>, _ ->
                selectedIndices.forEach {
                    createNewChipAndAddItToGroup(it.toString(), binding.productExtrasLayout.categoriesChipGroup)
                }
            }
            onNegative("Cancelar")
        }.show(parentFragmentManager, "")
    }
}