package com.puntogris.blint.feature_store.presentation.product.create_edit

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.BundleCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.maxkeppeler.sheets.options.DisplayMode
import com.maxkeppeler.sheets.options.Option
import com.maxkeppeler.sheets.options.OptionsSheet
import com.puntogris.blint.R
import com.puntogris.blint.common.utils.Keys
import com.puntogris.blint.common.utils.UiInterface
import com.puntogris.blint.common.utils.getInt
import com.puntogris.blint.common.utils.launchAndRepeatWithViewLifecycle
import com.puntogris.blint.common.utils.registerToolbarBackButton
import com.puntogris.blint.common.utils.setImageFullSize
import com.puntogris.blint.common.utils.setNumberIfNotZero
import com.puntogris.blint.common.utils.setProductCategoriesChip
import com.puntogris.blint.common.utils.setProductSuppliersChips
import com.puntogris.blint.common.utils.types.Resource
import com.puntogris.blint.common.utils.types.StringValidator
import com.puntogris.blint.common.utils.viewBinding
import com.puntogris.blint.databinding.FragmentEditProductBinding
import com.puntogris.blint.feature_store.domain.model.Category
import com.puntogris.blint.feature_store.domain.model.Trader
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditProductFragment : Fragment(R.layout.fragment_edit_product) {

    private val viewModel: EditProductViewModel by viewModels()
    private lateinit var scannerLauncher: ActivityResultLauncher<String>
    private lateinit var getContent: ActivityResultLauncher<Array<String>>

    private val binding by viewBinding(FragmentEditProductBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerToolbarBackButton(binding.toolbar)
        setupListeners()
        setupObservers()
        setupResultListeners()
        setupGalleryLauncher()
        setupScannerLauncher()
    }

    private fun setupObservers() {
        launchAndRepeatWithViewLifecycle {
            viewModel.currentProduct.collectLatest {
                binding.editProductDescriptionCard.editTextProductName.setText(it.product.name)

                with(binding.editProductPricesCard) {
                    groupInitialStockCard.isVisible = it.product.productId.isEmpty()
                    editTextProductStock.setNumberIfNotZero(it.product.stock)
                    editTextProductBuyPrice.setNumberIfNotZero(it.product.buyPrice)
                    editTextProductSellPrice.setNumberIfNotZero(it.product.sellPrice)
                    editTextProductSuggestedPrice.setNumberIfNotZero(it.product.suggestedSellPrice)
                }
                with(binding.editProductTradersCategoriesCard) {
                    chipAddSupplier.setOnClickListener {
                        navigateToProductSuppliers()
                    }
                    chipAddCateogies.setOnClickListener {
                        navigateToProductCategories()
                    }
                    chipGroupCategories.setProductCategoriesChip(it.categories)
                    chipGroupTraders.setProductSuppliersChips(it.traders)
                }
                with(binding.editProductExtrasCard) {
                    editTextProductSku.setText(it.product.sku)
                    editTextProductBrand.setText(it.product.brand)
                    editTextProductNotes.setText(it.product.notes)
                }
            }
        }
        launchAndRepeatWithViewLifecycle {
            viewModel.productImage.collectLatest {
                binding.editProductDescriptionCard.imageViewProductImage.setImageFullSize(it)
            }
        }
        launchAndRepeatWithViewLifecycle {
            viewModel.productBarcode.collectLatest {
                binding.editProductDescriptionCard.editTextProductBarcode.setText(it)
            }
        }
    }

    private fun setupListeners() {
        binding.buttonSave.setOnClickListener {
            onSaveButtonClicked()
        }
        binding.editProductDescriptionCard.imageViewProductImage.setOnClickListener {
            showImageOptions()
        }
        binding.editProductDescriptionCard.buttonOpenScanner.setOnClickListener {
            showScannerOptions()
        }
        binding.editProductPricesCard.imageViewDecreaseStockIcon.setOnClickListener {
            onDecreaseAmountButtonClicked()
        }
        binding.editProductPricesCard.imageViewIncreaseStockIcon.setOnClickListener {
            onIncreaseAmountButtonClicked()
        }
        binding.editProductDescriptionCard.editTextProductName.doAfterTextChanged { editable ->
            if (editable != null) {
                viewModel.updateProductName(editable)
            }
        }
        binding.editProductDescriptionCard.editTextProductBarcode.doAfterTextChanged { editable ->
            if (editable != null) {
                viewModel.updateProductBarcode(editable)
            }
        }
        binding.editProductPricesCard.editTextProductStock.doAfterTextChanged { editable ->
            if (editable != null) {
                viewModel.updateProductAmount(editable)
            }
        }
        binding.editProductPricesCard.editTextProductBuyPrice.doAfterTextChanged { editable ->
            if (editable != null) {
                viewModel.updateProductBuyPrice(editable)
            }
        }
        binding.editProductPricesCard.editTextProductSellPrice.doAfterTextChanged { editable ->
            if (editable != null) {
                viewModel.updateProductSellPrice(editable)
            }
        }
        binding.editProductPricesCard.editTextProductSuggestedPrice.doAfterTextChanged { editable ->
            if (editable != null) {
                viewModel.updateProductSuggestedPrice(editable)
            }
        }
        binding.editProductExtrasCard.editTextProductSku.doAfterTextChanged { editable ->
            if (editable != null) {
                viewModel.updateProductSku(editable)
            }
        }
        binding.editProductExtrasCard.editTextProductBrand.doAfterTextChanged { editable ->
            if (editable != null) {
                viewModel.updateProductBrand(editable)
            }
        }
        binding.editProductExtrasCard.editTextProductNotes.doAfterTextChanged { editable ->
            if (editable != null) {
                viewModel.updateProductNotes(editable)
            }
        }
    }

    fun onSaveButtonClicked() {
        val validator = StringValidator.from(
            viewModel.currentProduct.value.product.name,
            allowSpecialChars = true,
            isName = true,
            maxLength = 50
        )
        when (validator) {
            is StringValidator.Valid -> {
                lifecycleScope.launch {
                    val result = when (viewModel.saveProduct()) {
                        is Resource.Error -> R.string.snack_create_product_success
                        is Resource.Success -> {
                            findNavController().navigateUp()
                            R.string.snack_create_product_error
                        }
                    }
                    UiInterface.showSnackBar(getString(result))
                }
            }

            is StringValidator.NotValid -> UiInterface.showSnackBar(getString(validator.error))
        }
    }

    private fun setupResultListeners() {
        setFragmentResultListener(Keys.EDIT_PRODUCT_KEY) { _, bundle ->
            BundleCompat.getParcelableArrayList(
                bundle,
                Keys.PRODUCT_CATEGORIES_KEY,
                Category::class.java
            )?.let {
                viewModel.updateProductCategories(it)
            }
            BundleCompat.getParcelableArrayList(
                bundle,
                Keys.PRODUCT_SUPPLIERS_KEY,
                Trader::class.java
            )?.let {
                viewModel.updateProductSuppliers(it)
            }
        }
        setFragmentResultListener(Keys.SCANNER_RESULT_KEY) { _, bundle ->
            bundle.getString(Keys.PRODUCT_BARCODE_KEY)?.let {
                viewModel.updateProductBarcode(it)
            }
        }
    }

    private fun setupScannerLauncher() {
        scannerLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                if (isGranted) {
                    val action = EditProductFragmentDirections.actionGlobalScannerFragment(
                        returnResult = true
                    )
                    findNavController().navigate(action)
                } else {
                    UiInterface.showSnackBar(getString(R.string.snack_require_camera_permission))
                }
            }
    }

    private fun setupGalleryLauncher() {
        getContent = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            if (uri != null) {
                requireContext().contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                viewModel.updateProductImage(uri.toString())
            }
        }
    }

    fun onIncreaseAmountButtonClicked() {
        binding.editProductPricesCard.editTextProductStock.apply {
            setText(getInt().inc().toString())
        }
    }

    fun onDecreaseAmountButtonClicked() {
        binding.editProductPricesCard.editTextProductStock.apply {
            setText(getInt().dec().toString())
        }
    }

    fun navigateToProductSuppliers() {
        val action = EditProductFragmentDirections.actionEditProductFragmentToProductTraderFragment(
            viewModel.currentProduct.value.traders.toTypedArray()
        )
        findNavController().navigate(action)
    }

    fun navigateToProductCategories() {
        val action =
            EditProductFragmentDirections.actionEditProductFragmentToProductCategoryFragment(
                viewModel.currentProduct.value.categories.toTypedArray()
            )
        findNavController().navigate(action)
    }

    fun showScannerOptions() {
        OptionsSheet().show(requireParentFragment().requireContext()) {
            multipleChoices(false)
            displayMode(DisplayMode.LIST)
            with(
                Option(R.drawable.ic_fi_rr_scan, R.string.action_scan_barcode),
                Option(R.drawable.ic_fi_rr_refresh, R.string.action_generate_barcode)
            )
            onPositive { index: Int, _ ->
                if (index == 0) {
                    scannerLauncher.launch(Manifest.permission.CAMERA)
                } else {
                    viewModel.updateProductBarcode()
                }
            }
        }
    }

    fun showImageOptions() {
        OptionsSheet().show(requireParentFragment().requireContext()) {
            multipleChoices(false)
            displayMode(DisplayMode.LIST)
            with(
                Option(R.drawable.ic_fi_rr_camera, R.string.action_select_photo),
                Option(R.drawable.ic_fi_rr_trash, R.string.action_delete_photo)
            )
            onPositive { index: Int, _ ->
                if (index == 0) {
                    getContent.launch(arrayOf("image/*"))
                } else {
                    viewModel.updateProductImage("")
                }
            }
        }
    }
}
