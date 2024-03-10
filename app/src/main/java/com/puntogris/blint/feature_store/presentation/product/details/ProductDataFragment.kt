package com.puntogris.blint.feature_store.presentation.product.details

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.puntogris.blint.R
import com.puntogris.blint.common.utils.capitalizeFirstChar
import com.puntogris.blint.common.utils.launchAndRepeatWithViewLifecycle
import com.puntogris.blint.common.utils.setImageFullSize
import com.puntogris.blint.common.utils.setProductCategoriesChip
import com.puntogris.blint.common.utils.setProductSuppliersChips
import com.puntogris.blint.common.utils.setTextOrDefault
import com.puntogris.blint.common.utils.viewBinding
import com.puntogris.blint.databinding.FragmentDataProductBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ProductDataFragment : Fragment(R.layout.fragment_data_product) {

    private val viewModel: ProductViewModel by viewModels(ownerProducer = { requireParentFragment() })

    private val binding by viewBinding(FragmentDataProductBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
    }

    private fun setupObservers() {
        launchAndRepeatWithViewLifecycle {
            viewModel.currentProduct.collectLatest {
                with(binding) {
                    imageViewProductImage.setImageFullSize(it.product.image)
                    textViewProductName.text = it.product.name.capitalizeFirstChar()
                    textViewProductBarcode.setTextOrDefault(it.product.barcode)
                    textViewProductBuyPrice.setTextOrDefault(it.product.buyPrice)
                    textViewProductSellPrice.setTextOrDefault(it.product.sellPrice)
                    textViewProductSuggestedPrice.setTextOrDefault(it.product.suggestedSellPrice)
                    textViewProductSku.setTextOrDefault(it.product.sku)
                    textViewProductBrand.setTextOrDefault(it.product.brand)
                    textViewProductNotes.setTextOrDefault(it.product.notes)
                    textViewProductStock.text = it.product.stock.toString()
                    textViewProductCategoriesSummary.setTextOrDefault(it.categories)
                    textViewProductSuppliersSummary.setTextOrDefault(it.traders)
                    chipGroupProductSuppliers.setProductSuppliersChips(it.traders)
                    chipGroupProductCategories.setProductCategoriesChip(it.categories)
                }
            }
        }
    }
}
