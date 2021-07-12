package com.puntogris.blint.ui.reports

import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentGenerateReportBinding
import com.puntogris.blint.model.ClientRecordExcel
import com.puntogris.blint.model.ProductRecordExcel
import com.puntogris.blint.model.SupplierRecordExcel
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.*
import com.puntogris.blint.utils.Constants.CLIENTS_LIST
import com.puntogris.blint.utils.Constants.CLIENTS_RECORDS
import com.puntogris.blint.utils.Constants.PRODUCTS_LIST
import com.puntogris.blint.utils.Constants.PRODUCTS_RECORDS
import com.puntogris.blint.utils.Constants.SUPPLIERS_LIST
import com.puntogris.blint.utils.Constants.SUPPLIERS_RECORDS
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.openxmlformats.schemas.spreadsheetml.x2006.main.WorkbookDocument
import java.util.*

@Suppress("BlockingMethodInNonBlockingContext")
@AndroidEntryPoint
class GenerateReportFragment:
    BaseFragment<FragmentGenerateReportBinding>(R.layout.fragment_generate_report) {

    private val viewModel: ReportsViewModel by viewModels()
    private val args: GenerateReportFragmentArgs by navArgs()
    private lateinit var activityResultLauncher: ActivityResultLauncher<String>

    override fun initializeViews() {
        setUpUi(showFab = true, fabIcon = R.drawable.ic_baseline_share_24){
            val uri = viewModel.getDownloadUri()
            if (uri == null)
                showLongSnackBarAboveFab("Todavia no se genero el informe o ocurrio un error.")
            else shareFileIntent(uri)
        }

        launchAndRepeatWithViewLifecycle {
            viewModel.exportingState.collect {
                when(it){
                    is ExportResult.Error -> showErrorUi()
                    ExportResult.InProgress -> showInProgressUi()
                    ExportResult.Success -> showSuccessUi()
                }
            }
        }
        var fileName = ""
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.CreateDocument()){ uri->
            when (args.reportCode) {
                SUPPLIERS_LIST -> {
                    fileName = "proveedores_lista_${Date().getDateFormattedString()}"
                    exportSupplierList(uri)
                }
                CLIENTS_LIST -> {
                    fileName = "clientes_lista_${Date().getDateFormattedString()}"
                    exportClientList(uri)
                }
                PRODUCTS_LIST -> {
                    fileName = "productos_lista_${Date().getDateFormattedString()}"
                    exportProductList(uri)
                }
                PRODUCTS_RECORDS -> {
                    fileName = "productos_movimientos_${Date().getDateFormattedString()}"
                    exportProductRecords(uri)
                }
//                SUPPLIERS_RECORDS -> {
//                    fileName = "proveedores_movimientos_${Date().getDateFormattedString()}"
//                    exportSupplierRecords(uri)
//                }
//                CLIENTS_RECORDS -> {
//                    fileName = "clientes_movimientos_${Date().getDateFormattedString()}"
//                    exportClientRecords(uri)
//                }
            }
        }
        activityResultLauncher.launch("$fileName.xls")
    }

    private fun showInProgressUi(){}

    private fun showSuccessUi(){
        binding.reportTitle.text = "Reporte exportado correctamente"
        binding.reportMessage.text = "*Compartilo con quien vos quieras a traves de varias aplicaciones."
        binding.progressBar.gone()
        binding.animationView.visible()
        binding.animationView.playAnimation()
    }

    private fun showErrorUi(){
        binding.progressBar.gone()
        binding.reportTitle.text = "Ocurrio un error al exportar el informe"
        showLongSnackBarAboveFab("Ocurrio un error en la generacion del informe. Intente nuevamente.")
    }

    private fun exportProductRecords(downloadFileUri: Uri) {
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Products")
        val row0 = sheet.createRow(0)
        row0.createCell(0).setCellValue("Product")
        row0.createCell(1).setCellValue("In")
        row0.createCell(2).setCellValue("Out")
        var numberOfRows = 0

        lifecycleScope.launch {
            when(val records = viewModel.getProductRecords(args.timeCode, args.startTime, args.endTime)){
                is RepoResult.Error -> {}
                RepoResult.InProgress -> {}
                is RepoResult.Success -> {
                    try {
                        records.data.sortedBy { it.productName }
                        records.data.forEach {
                            numberOfRows += 1
                            val newRow = sheet.createRow(numberOfRows)
                            newRow.createCell(0).setCellValue(it.productName)
                            newRow.createCell(1).setCellValue(it.totalInStock.toString())
                            newRow.createCell(2).setCellValue(it.totalOutStock.toString())
                        }
                        workbook.writeToFile(downloadFileUri)
                        viewModel.updateExportState(ExportResult.Success)
                    }catch (e: Exception) {
                        viewModel.updateExportState(ExportResult.Error(e))
                    }
                }
            }
        }
    }

    private fun shareFileIntent(uri: Uri) {
        val share = Intent(Intent.ACTION_SEND)
        share.type = "application/vnd.ms-excel"
        share.putExtra(Intent.EXTRA_STREAM, uri)
        startActivity(Intent.createChooser(share, "Compartir informe"))
    }

    private fun exportSupplierList(downloadFileUri: Uri) {
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Proveedores")
        var numberOfRows = 0
        val row0 = sheet.createRow(numberOfRows)
        row0.createCell(0).setCellValue("Nombre")
        row0.createCell(1).setCellValue("Telefono")
        row0.createCell(2).setCellValue("Direccion")
        row0.createCell(3).setCellValue("E-mail")

        lifecycleScope.launch {
            try {
                when(val suppliers = viewModel.getAllSuppliersData()){
                    is RepoResult.Error -> {}
                    RepoResult.InProgress -> {}
                    is RepoResult.Success -> {
                        suppliers.data.forEach {
                            numberOfRows += 1
                            val row = sheet.createRow(numberOfRows)
                            row.createCell(0).setCellValue(it.companyName)
                            row.createCell(1).setCellValue(it.companyPhone)
                            row.createCell(2).setCellValue(it.address)
                            row.createCell(3).setCellValue(it.companyEmail)
                        }
                        workbook.writeToFile(downloadFileUri)
                        viewModel.updateExportState(ExportResult.Success)
                    }
                }
            }catch (e:Exception){ viewModel.updateExportState(ExportResult.Error(e)) }
        }
    }

    private fun exportClientList(downloadFileUri: Uri) {
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Clientes")
        var numberOfRows = 0
        val row0 = sheet.createRow(numberOfRows)
        row0.createCell(0).setCellValue("Nombre")
        row0.createCell(1).setCellValue("Telefono")
        row0.createCell(2).setCellValue("Direccion")
        row0.createCell(3).setCellValue("E-mail")

        lifecycleScope.launch {
            try {
                when(val suppliers = viewModel.getAllClientsData()){
                    is RepoResult.Error -> {}
                    RepoResult.InProgress -> {}
                    is RepoResult.Success -> {
                        suppliers.data.forEach {
                            numberOfRows += 1
                            val row = sheet.createRow(numberOfRows)
                            row.createCell(0).setCellValue(it.name)
                            row.createCell(1).setCellValue(it.phone)
                            row.createCell(2).setCellValue(it.address)
                            row.createCell(3).setCellValue(it.email)
                        }
                        workbook.writeToFile(downloadFileUri)
                        viewModel.updateExportState(ExportResult.Success)
                    }
                }
            }catch (e:Exception){ viewModel.updateExportState(ExportResult.Error(e)) }
        }
    }

    private fun exportProductList(downloadFileUri: Uri) {
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Productos")
        var numberOfRows = 0
        val row0 = sheet.createRow(numberOfRows)
        row0.createCell(0).setCellValue("Nombre")
        row0.createCell(1).setCellValue("Stock")
        row0.createCell(2).setCellValue("Codigo de barras")
        row0.createCell(3).setCellValue("Precio compra")
        row0.createCell(4).setCellValue("Precio venta")
        row0.createCell(5).setCellValue("Precio venta sugerido")
        row0.createCell(6).setCellValue("Codigo interno")
        row0.createCell(7).setCellValue("Marca")
        row0.createCell(8).setCellValue("Tamano")

        lifecycleScope.launch {
            try {
                when(val suppliers = viewModel.getAllProductsData()){
                    is RepoResult.Error -> {}
                    RepoResult.InProgress -> {}
                    is RepoResult.Success -> {
                        suppliers.data.forEach {
                            numberOfRows += 1
                            val row = sheet.createRow(numberOfRows)
                            row.createCell(0).setCellValue(it.name)
                            row.createCell(1).setCellValue(it.amount.toString())
                            row.createCell(2).setCellValue(it.barcode)
                            row.createCell(3).setCellValue(it.buyPrice.toString())
                            row.createCell(4).setCellValue(it.sellPrice.toString())
                            row.createCell(5).setCellValue(it.suggestedSellPrice.toString())
                            row.createCell(6).setCellValue(it.internalCode)
                            row.createCell(7).setCellValue(it.brand)
                            row.createCell(8).setCellValue(it.size)

                        }
                        workbook.writeToFile(downloadFileUri)
                        viewModel.updateExportState(ExportResult.Success)
                    }
                }
            }catch (e:Exception){ viewModel.updateExportState(ExportResult.Error(e)) }
        }
    }

    private suspend fun XSSFWorkbook.writeToFile(uri: Uri){
        withContext(Dispatchers.IO) {
            val file = requireActivity().contentResolver.openOutputStream(uri)
            write(file)
            file?.close()
            viewModel.saveDownloadUri(uri)
        }
    }

//    private fun exportSupplierRecords(downloadFileUri: Uri) {
//        val workbook = XSSFWorkbook()
//        val sheet = workbook.createSheet("Supplier")
//        val row0 = sheet.createRow(0)
//        row0.createCell(0).setCellValue("Supplier")
//        row0.createCell(1).setCellValue("Product")
//        row0.createCell(2).setCellValue("In")
//        var numberOfRows = 0
//
//        lifecycleScope.launch {
//            when(val records = viewModel.getSuppliersRecords(args.timeCode, args.startTime, args.endTime)){
//                is RepoResult.Error -> {}
//                RepoResult.InProgress -> {}
//                is RepoResult.Success -> {
//                    try {
//                        val sortedSuppliers = records.data.sortedBy { it.supplierName }
//
//                        sortedSuppliers.forEach { client->
//                            numberOfRows += 1
//                            val newRow = sheet.createRow(numberOfRows)
//                            newRow.createCell(0).setCellValue(client.supplierName)
//                            client.products.forEach {
//                                numberOfRows += 1
//                                val productRow = sheet.createRow(numberOfRows)
//                                productRow.createCell(1).setCellValue(it.productName)
//                                productRow.createCell(2).setCellValue(it.amount.toString())
//                            }
//                        }
//                        workbook.writeToFile(downloadFileUri)
//                        viewModel.updateExportState(ExportResult.Success)
//                    }catch (e:Exception) {
//                        viewModel.updateExportState(ExportResult.Error(e))
//                    }
//                }
//            }
//        }
//    }
//
//    private fun exportClientRecords(downloadFileUri: Uri) {
//        val workbook = XSSFWorkbook()
//        val sheet = workbook.createSheet("Clients")
//        val row0 = sheet.createRow(0)
//        row0.createCell(0).setCellValue("Client")
//        row0.createCell(1).setCellValue("Product")
//        row0.createCell(2).setCellValue("Out")
//        var numberOfRows = 0
//
//        lifecycleScope.launch {
//            when(val records = viewModel.getClientRecords(args.timeCode, args.startTime, args.endTime)){
//                is RepoResult.Error -> {}
//                RepoResult.InProgress -> {}
//                is RepoResult.Success -> {
//                    try {
//                        val sortedSuppliers = records.data.sortedBy { it.clientName }
//
//                        sortedSuppliers.forEach { client->
//                            numberOfRows += 1
//                            val newRow = sheet.createRow(numberOfRows)
//                            newRow.createCell(0).setCellValue(client.clientName)
//                            client.products.forEach {
//                                numberOfRows += 1
//                                val productRow = sheet.createRow(numberOfRows)
//                                productRow.createCell(1).setCellValue(it.productName)
//                                productRow.createCell(2).setCellValue(it.amount.toString())
//                            }
//                        }
//                        workbook.writeToFile(downloadFileUri)
//                        viewModel.updateExportState(ExportResult.Success)
//                    }catch (e:Exception) {
//                        viewModel.updateExportState(ExportResult.Error(e))
//                    }
//                }
//            }
//        }
//    }
}