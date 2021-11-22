package com.puntogris.blint.ui.scanner

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.puntogris.blint.data.repository.products.ProductRepository
import com.puntogris.blint.model.product.Product
import com.puntogris.blint.model.product.ProductWithDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ScannerViewModel @Inject constructor(
    private val repository: ProductRepository,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    private val barcode = savedStateHandle.get<String>("barcode") ?: ""

    val barcodeScanned = liveData { emit(barcode) }

    val product = liveData {
        emit(repository.getProductWithBarcode(barcode) ?: ProductWithDetails())
    }
}