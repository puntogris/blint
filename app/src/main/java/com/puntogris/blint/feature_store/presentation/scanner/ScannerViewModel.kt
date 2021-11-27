package com.puntogris.blint.feature_store.presentation.scanner

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.puntogris.blint.feature_store.domain.model.product.ProductWithDetails
import com.puntogris.blint.feature_store.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ScannerViewModel @Inject constructor(
    private val repository: ProductRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val barcode = savedStateHandle.get<String>("barcode") ?: ""

    val barcodeScanned = liveData { emit(barcode) }

    val product = liveData {
        emit(repository.getProductWithBarcode(barcode) ?: ProductWithDetails())
    }
}