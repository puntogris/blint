package com.puntogris.blint.ui.reports

import android.net.Uri
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.puntogris.blint.data.local.dao.StatisticsDao
import com.puntogris.blint.data.remote.StatisticRepository
import com.puntogris.blint.model.Record
import com.puntogris.blint.utils.ExportResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ReportsViewModel @Inject constructor(
    private val statisticsDao: StatisticsDao,
    private val statisticRepository: StatisticRepository
):ViewModel() {

    suspend fun getStatistics() = statisticsDao.getStatistics()

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

    suspend fun getProductRecords(timeCode:String, startTime:Long, endTime:Long):List<Record> {
        return when(timeCode){
            "WEEKLY" -> statisticsDao.getRecordsWithDays("-7 days")
            "MONTHLY" -> statisticsDao.getRecordsWithDays("-30 days")
            "QUARTERLY" -> statisticsDao.getRecordsWithDays("-90 days")
            "BIANNUAL" -> statisticsDao.getRecordsWithDays("-180 days")
            "ANNUAL" -> statisticsDao.getRecordsWithDays("-360 days")
            "HISTORICAL" -> statisticsDao.getAllRecords()
            else -> statisticsDao.getRecordsWithDaysFrame(startTime, endTime)
        }
    }

    suspend fun getClientRecords(timeCode:String, startTime:Long, endTime:Long):List<Record> {
        return when(timeCode){
             "WEEKLY" -> statisticsDao.getRecordsClientsWithDays("-7 days")
             "MONTHLY" -> statisticsDao.getRecordsClientsWithDays("-30 days")
             "QUARTERLY" -> statisticsDao.getRecordsClientsWithDays("-90 days")
             "BIANNUAL" -> statisticsDao.getRecordsClientsWithDays("-180 days")
             "ANNUAL" -> statisticsDao.getRecordsClientsWithDays("-360 days")
             "HISTORICAL" -> statisticsDao.getAllClientsRecords()
             else -> statisticsDao.getRecordsClientsWithDaysFrame(startTime, endTime)
         }
    }

    suspend fun getSuppliersRecords(timeCode:String, startTime:Long, endTime:Long):List<Record> {
        return when(timeCode){
             "WEEKLY" -> statisticsDao.getRecordsSuppliersWithDays("-7 days")
             "MONTHLY" -> statisticsDao.getRecordsSuppliersWithDays("-30 days")
             "QUARTERLY" -> statisticsDao.getRecordsSuppliersWithDays("-90 days")
             "BIANNUAL" -> statisticsDao.getRecordsSuppliersWithDays("-180 days")
             "ANNUAL" -> statisticsDao.getRecordsSuppliersWithDays("-360 days")
             "HISTORICAL" -> statisticsDao.getAllSuppliersRecords()
             else -> statisticsDao.getRecordsSuppliersWithDaysFrame(startTime, endTime)
         }
    }

}