package com.puntogris.blint.feature_store.presentation.product.create_edit

import android.Manifest
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.puntogris.blint.R
import com.puntogris.blint.common.presentation.base.BaseFragment
import com.puntogris.blint.common.utils.*
import com.puntogris.blint.common.utils.types.SimpleResult
import com.puntogris.blint.common.utils.types.StringValidator
import com.puntogris.blint.databinding.FragmentEditProductBinding
import com.puntogris.blint.feature_store.domain.model.Category
import com.puntogris.blint.feature_store.domain.model.Supplier
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditProductFragment :
    BaseFragment<FragmentEditProductBinding>(R.layout.fragment_edit_product) {

    private val viewModel: EditProductViewModel by viewModels()
    lateinit var scannerLauncher: ActivityResultLauncher<String>
    private lateinit var getContent: ActivityResultLauncher<String>

    override fun initializeViews() {
        binding.fragment = this
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        UiInterface.registerUi(showFab = true, fabIcon = R.drawable.ic_baseline_save_24) {
            saveProduct()
        }

        //todo use databinding for this or remove it
        if (viewModel.currentProduct.value.product.productId.isEmpty()) {
            binding.pricesLayout.apply {
                productAmount.visible()
                increaseAmountButton.visible()
                decreaseAmountButton.visible()
                productPricesTitle.text = getString(R.string.prices_and_initial_stock)
            }
        }

        setupResultListeners()
        setupGalleryLauncher()
        setupScannerLauncher()
    }

    private fun saveProduct() {
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
                        SimpleResult.Failure -> R.string.snack_create_product_success
                        SimpleResult.Success -> {
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
            bundle.getParcelableArrayList<Category>(Keys.PRODUCT_CATEGORIES_KEY)?.let {
                viewModel.updateProductCategories(it)
            }
            bundle.getParcelableArrayList<Supplier>(Keys.PRODUCT_SUPPLIERS_KEY)?.let {
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
        scannerLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission())
        { isGranted: Boolean ->
            if (isGranted) {
                val action = EditProductFragmentDirections
                    .actionEditProductFragmentToScannerFragment(1)
                findNavController().navigate(action)
            } else {
                UiInterface.showSnackBar(getString(R.string.snack_require_camera_permission))
            }
        }
    }

    private fun setupGalleryLauncher() {
        getContent = registerForActivityResult(ActivityResultContracts.GetContent()) {
            viewModel.updateProductImage(it.toString())
        }
    }

    fun onScanButtonClicked() {
        scannerLauncher.launch(Manifest.permission.CAMERA)
    }

    fun onAddImageButtonClicked() {
        getContent.launch("image/*")
    }

    fun onRemoveImageButtonClicked() {
        viewModel.updateProductImage("")
    }

    fun onIncreaseAmountButtonClicked() {
        binding.pricesLayout.productAmountText.apply {
            setText(getInt().inc().toString())
        }
    }

    fun onDecreaseAmountButtonClicked() {
        binding.pricesLayout.productAmountText.apply {
            setText(getInt().dec().toString())
        }
    }

    fun navigateToProductSuppliers() {
        val action = EditProductFragmentDirections
            .actionEditProductFragmentToProductSupplierFragment(viewModel.currentProduct.value.suppliers.toTypedArray())
        findNavController().navigate(action)
    }

    fun navigateToProductCategories() {
        val action = EditProductFragmentDirections
            .actionEditProductFragmentToProductCategoryFragment(viewModel.currentProduct.value.categories.toTypedArray())
        findNavController().navigate(action)
    }
}