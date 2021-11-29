package com.puntogris.blint.feature_store.presentation.reports

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.puntogris.blint.common.utils.Constants
import com.puntogris.blint.common.utils.types.ExportResult
import com.puntogris.blint.feature_store.domain.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ReportsViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val supplierRepository: SupplierRepository,
    private val clientRepository: ClientRepository
) : ViewModel() {

    fun updateTimeFrame(timeFrame: TimeFrame){

    }

    fun updateReportType(type:Int){

    }

    val type = 4
    fun asd(){
        when(type){
            Constants.PRODUCTS_RECORDS -> {}
            Constants.CLIENTS_RECORDS -> {}
            Constants.SUPPLIERS_RECORDS -> {}
            Constants.PRODUCTS_LIST ->{}
            Constants.CLIENTS_LIST -> {}
            Constants.SUPPLIERS_LIST -> {}
        }
    }

    private var saveDownloadFileUri: Uri? = null

    fun saveDownloadUri(uri: Uri) {
        saveDownloadFileUri = uri
    }

    fun getDownloadUri() = saveDownloadFileUri

    private val _exportingState = MutableStateFlow<ExportResult>(ExportResult.InProgress)
    val exportingState: StateFlow<ExportResult> = _exportingState

    fun updateExportState(result: ExportResult) {
        _exportingState.value = result
    }

   // suspend fun getProductRecords(timeCode: String) = statisticRepository.saveProductsRecords(timeCode)

    suspend fun getAllClientsData() = clientRepository.getClients()

    suspend fun getAllSuppliersData() = supplierRepository.getSuppliers()

    suspend fun getAllProductsData() = productRepository.getProducts()

//    suspend fun getClientRecords(timeCode:String, startTime:Long, endTime:Long) =
//        clientRepository.getClientsReports(timeCode, startTime, endTime)
//
//    suspend fun getSuppliersRecords(timeCode:String, startTime:Long, endTime:Long) =
//        supplierRepository.getSuppliersReports(timeCode, startTime, endTime)

}

