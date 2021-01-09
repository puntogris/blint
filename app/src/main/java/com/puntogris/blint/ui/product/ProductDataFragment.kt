package com.puntogris.blint.ui.product

import android.content.Intent
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import com.maxkeppeler.bottomsheets.options.DisplayMode
import com.maxkeppeler.bottomsheets.options.Option
import com.maxkeppeler.bottomsheets.options.OptionsSheet
import com.nguyenhoanglam.imagepicker.model.Config
import com.nguyenhoanglam.imagepicker.ui.imagepicker.ImagePicker
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentDataProductBinding
import com.puntogris.blint.model.Product
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.handleCoroutineException
import javax.inject.Inject

@Suppress("DEPRECATION")
@AndroidEntryPoint
class ProductDataFragment : BaseFragment<FragmentDataProductBinding>(R.layout.fragment_data_product) {

    private val viewModel: ProductViewModel by viewModels()
    @Inject
    lateinit var permissionsManager: PermissionsManager

    override fun initializeViews() {
        binding.fragment = this
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        arguments?.takeIf { it.containsKey("data_key") }?.apply {
            getParcelable<Product>("data_key")?.let { productBundle->
                viewModel.currentProduct.value.let { savedProduct ->
                    if (productBundle.id != savedProduct?.id){
                        viewModel.setCurrentProductData(productBundle)
                        viewModel.updateProductImage(productBundle.image)
                    }
                }
            }
        }

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<String>("key")?.observe(
            viewLifecycleOwner) { result ->
            binding.descriptionLayout.productBarcodeText.setText(result)
        }

        getParentFab().apply {
            changeIconFromDrawable(R.drawable.ic_baseline_save_24)
            setOnClickListener {
                viewModel.updateCurrentProductData(getProductDataFromViews())
                when(val validator = StringValidator.from(viewModel.currentProduct.value!!.name, allowSpecialChars = true)){
                    is StringValidator.Valid -> {
                        viewModel.saveCurrentProductToDatabase()
                        createShortSnackBar("Se guardo el producto satisfactoriamente.").setAnchorView(this).show()
                        findNavController().navigateUp()
                    }
                    is StringValidator.NotValid -> createShortSnackBar(validator.error).setAnchorView(this).show()
                }
            }
        }
    }

    private fun getProductDataFromViews(): Product{
        return Product(
            name = binding.descriptionLayout.productNameText.getString(),
            barcode = binding.descriptionLayout.productBarcodeText.getString(),
            description = binding.descriptionLayout.productDescriptionText.getString(),
            buyPrice = binding.pricesLayout.productBuyPriceText.getFloat(),
            sellPrice = binding.pricesLayout.productSellPriceText.getFloat(),
            suggestedSellPrice = binding.pricesLayout.productSuggestedSellPriceText.getFloat(),
            amount = binding.pricesLayout.productAmountText.getFloat(),
            image = viewModel.productImage.value!!,
            internalCode = binding.extrasLayout.productInternalCodeText.getString(),
            brand = binding.extrasLayout.productBrandText.getString(),
            size = binding.extrasLayout.productSizeText.getString()
        )
    }

    fun onSearchButtonClicked(){

    }

    fun onScanButtonClicked(){
        viewModel.updateCurrentProductData(getProductDataFromViews())
        val productFragment = requireParentFragment() as ProductFragment
        permissionsManager.requestCameraPermissionAndNavigateToScaner(productFragment)
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


    fun onPricesButtonClicked(){
        binding.productPricesButton.toggleIcon()
        binding.pricesLayout.expandableLayout.toggle()
        hideKeyboard()
    }

    fun onDescriptionButtonClicked(){
        binding.productDescriptionButton.toggleIcon()
        binding.descriptionLayout.expandableLayout.toggle()
        hideKeyboard()
    }

    fun onImageButtonClicked(){
        binding.imagesLayout.expandableLayout.toggle()
        binding.productImageButton.toggleIcon()
        hideKeyboard()
    }

    fun onRemoveImageButtonClicked(){
        viewModel.removeCurrentImage()
        binding.imagesLayout.expandableLayout.toggle()
    }

    fun onScopeButtonClicked(){
        binding.scopeLayout.expandableLayout.toggle()
        binding.productScopeButton.toggleIcon()
        hideKeyboard()
    }
    fun onExtrasButtonClicked(){
        binding.productAdvanceButton.toggleIcon()
        binding.extrasLayout.expandableLayout.toggle()
        hideKeyboard()
    }

    fun onIncreaseAmountButtonClicked(){
        binding.pricesLayout.productAmountText.apply {
            setText(getFloat().inc().toString())
        }
    }
    fun onDecreaseAmountButtonClicked(){
        binding.pricesLayout.productAmountText.apply {
            setText(getFloat().dec().toString())
        }
    }
    fun openBottomSheetForClients(){
        OptionsSheet().build(requireContext()){
            title("Agregar Clientes")
            displayMode(DisplayMode.LIST)
            multipleChoices()
            with(
                Option("Cliente 1"),
                Option("Cliente 2"),
                Option("Cliente 3"),
                Option("Cliente 4"),
                Option("Cliente 5"),
                Option("Cliente 6")
            )
            onPositiveMultiple("Agregar") { selectedIndices: MutableList<Int>, _ ->
                selectedIndices.forEach {
                    createNewChipAndAddItToGroup(it.toString(), binding.scopeLayout.clientsChipGroup)
                }
            }
            onNegative("Cancelar")
        }.show(parentFragmentManager, "")
    }

    fun openBottomSheetForSuppliers(){
        OptionsSheet().build(requireContext()){
            title("Agregar Proveedores")
            displayMode(DisplayMode.LIST)
            multipleChoices()
            with(
                Option("Proveedor 1"),
                Option("Proveedor 2"),
                Option("Proveedor 3"),
                Option("Proveedor 4"),
                Option("Proveedpor 5"),
                Option("Proveedor 6")
            )
            onPositiveMultiple("Agregar") { selectedIndices: MutableList<Int>, _ ->
                selectedIndices.forEach {
                    createNewChipAndAddItToGroup(it.toString(), binding.scopeLayout.supplierChipGroup)
                }
            }
            onNegative("Cancelar")
        }.show(parentFragmentManager, "")
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
                    createNewChipAndAddItToGroup(it.toString(), binding.extrasLayout.categoriesChipGroup)
                }
            }
            onNegative("Cancelar")
        }.show(parentFragmentManager, "")
    }

}