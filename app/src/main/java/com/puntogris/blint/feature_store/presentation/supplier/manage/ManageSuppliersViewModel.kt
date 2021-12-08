package com.puntogris.blint.feature_store.presentation.supplier.manage

import android.text.Editable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.puntogris.blint.feature_store.domain.repository.SupplierRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class ManageSuppliersViewModel @Inject constructor(
    private val repository: SupplierRepository
) : ViewModel() {

    private val query = MutableStateFlow("")

    @OptIn(ExperimentalCoroutinesApi::class)
    val suppliersFlow = query.flatMapLatest {
        repository.getSuppliersPaged(it)
    }.cachedIn(viewModelScope)

    fun setQuery(editable: Editable) {
        query.value = editable.toString()
    }
}