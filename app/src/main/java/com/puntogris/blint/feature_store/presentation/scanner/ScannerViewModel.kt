package com.puntogris.blint.feature_store.presentation.scanner

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.puntogris.blint.feature_store.domain.model.product.Product
import com.puntogris.blint.feature_store.domain.model.product.ProductWithDetails
import com.puntogris.blint.feature_store.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class ScannerViewModel @Inject constructor(
    private val repository: ProductRepository,
    handle: SavedStateHandle
) : ViewModel() {

    val scannedProduct = handle.getStateFlow("barcode", "").map {
        repository.getProductWithBarcode(it) ?: ProductWithDetails(Product(barcode = it))
    }
}
