package com.puntogris.blint.feature_store.presentation.scanner

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import com.puntogris.blint.feature_store.domain.model.product.Product
import com.puntogris.blint.feature_store.domain.model.product.ProductWithDetails
import com.puntogris.blint.feature_store.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ScannerViewModel @Inject constructor(
    private val repository: ProductRepository,
    handle: SavedStateHandle
) : ViewModel() {

    val scannedProduct = handle.getLiveData<String>("barcode").switchMap {
        liveData {
            emit(repository.getProductWithBarcode(it) ?: ProductWithDetails(Product(barcode = it)))
        }
    }
}