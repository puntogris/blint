package com.puntogris.blint.ui.reports

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.puntogris.blint.data.repo.statistics.StatisticRepository
import com.puntogris.blint.utils.ExportResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ReportsViewModel @Inject constructor(
    private val statisticRepository: StatisticRepository
):ViewModel() {

    suspend fun getStatistics() = statisticRepository.getBusinessCounters()

    private var saveDownloadFileUri:Uri? = null

    fun saveDownloadUri(uri: Uri){
        saveDownloadFileUri = uri
    }

    fun getDownloadUri() = saveDownloadFileUri

    private val _exportingState = MutableStateFlow<ExportResult>(ExportResult.InProgress)
    val exportingState:StateFlow<ExportResult> = _exportingState

    fun updateExportState(result: ExportResult){
        _exportingState.value = result
    }

    suspend fun getProductRecords(timeCode:String) =
        statisticRepository.getProductsReports(timeCode)

    suspend fun getAllClientsData() = statisticRepository.getAllClients()

    suspend fun getAllSuppliersData() = statisticRepository.getAllSuppliers()

    suspend fun getAllProductsData() = statisticRepository.getAllProducts()

//    suspend fun getClientRecords(timeCode:String, startTime:Long, endTime:Long) =
//        statisticRepository.getClientsReports(timeCode, startTime, endTime)
//
//    suspend fun getSuppliersRecords(timeCode:String, startTime:Long, endTime:Long) =
//        statisticRepository.getSuppliersReports(timeCode, startTime, endTime)

}