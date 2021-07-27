package com.puntogris.blint.ui.reports

import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentGenerateReportBinding
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.*
import com.puntogris.blint.utils.Constants.CLIENTS_LIST
import com.puntogris.blint.utils.Constants.PRODUCTS_LIST
import com.puntogris.blint.utils.Constants.PRODUCTS_RECORDS
import com.puntogris.blint.utils.Constants.SUPPLIERS_LIST
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.util.*

@Suppress("BlockingMethodInNonBlockingContext")
@AndroidEntryPoint
class GenerateReportFragment:
    BaseFragment<FragmentGenerateReportBinding>(R.layout.fragment_generate_report) {

    private val viewModel: ReportsViewModel by viewModels()
    private val args: GenerateReportFragmentArgs by navArgs()
    private lateinit var activityResultLauncher: ActivityResultLauncher<String>

    override fun initializeViews() {
        registerUiInterface.register(showFab = true, fabIcon = R.drawable.ic_baseline_share_24){
            val uri = viewModel.getDownloadUri()
            if (uri == null)
                showLongSnackBarAboveFab(getString(R.string.snack_report_generating_or_error))
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

        val date = Date().getDateFormattedStringUnderLine()
        val fileName = when(args.reportCode){
            SUPPLIERS_LIST -> getString(R.string.report_list_suppliers, date)
            CLIENTS_LIST -> getString(R.string.report_list_clients, date)
            PRODUCTS_LIST -> getString(R.string.report_list_products, date)
            PRODUCTS_RECORDS -> getString(R.string.report_product_records, date)
            else -> ""
        }

        activityResultLauncher = registerForActivityResult(ActivityResultContracts.CreateDocument()){ uri->
            if (uri != null){
                println(args.reportCode)
                when (args.reportCode) {
                    SUPPLIERS_LIST -> exportSupplierList(uri)
                    CLIENTS_LIST -> exportClientList(uri)
                    PRODUCTS_LIST -> exportProductList(uri)
                    PRODUCTS_RECORDS -> exportProductRecords(uri)
//                SUPPLIERS_RECORDS -> {
//                    fileName = "proveedores_movimientos_${Date().getDateFormattedString()}"
//                    exportSupplierRecords(uri)
//                }
//                CLIENTS_RECORDS -> {
//                    fileName = "clientes_movimientos_${Date().getDateFormattedString()}"
//                    exportClientRecords(uri)
//                }
                }
            }else findNavController().navigateUp()
        }
        activityResultLauncher.launch("${fileName}.xls")
    }

    private fun showInProgressUi(){}

    private fun showSuccessUi(){
        binding.reportTitle.text = getString(R.string.report_exported_success_title)
        binding.reportMessage.text = getString(R.string.report_exported_success_message)
        binding.progressBar.gone()
        binding.animationView.visible()
        binding.animationView.playAnimation()
    }

    private fun showErrorUi(){
        binding.progressBar.gone()
        binding.reportTitle.text = getString(R.string.report_exported_error_title)
        showLongSnackBarAboveFab(getString(R.string.report_exported_error_message))
    }

    private fun exportProductRecords(downloadFileUri: Uri) {
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet(getString(R.string.products_label))
        val row0 = sheet.createRow(0)
        row0.createCell(0).setCellValue(getString(R.string.product))
        row0.createCell(1).setCellValue(getString(R.string.in_entry))
        row0.createCell(2).setCellValue(getString(R.string.out_entry))
        var numberOfRows = 0

        lifecycleScope.launch {
            when(val records = viewModel.getProductRecords(args.timeCode)){
                is RepoResult.Error -> {}
                RepoResult.InProgress -> {}
                is RepoResult.Success -> {
                    try {
                        records.data.sortedBy { it.name }
                        records.data.forEach {

                            numberOfRows += 1
                            val newRow = sheet.createRow(numberOfRows)
                            newRow.createCell(0).setCellValue(it.name)
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
        startActivity(Intent.createChooser(share, getString(R.string.action_share_report)))
    }

    private fun exportSupplierList(downloadFileUri: Uri) {
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet(getString(R.string.suppliers_label))
        var numberOfRows = 0
        val row0 = sheet.createRow(numberOfRows)
        row0.createCell(0).setCellValue(getString(R.string.name))
        row0.createCell(1).setCellValue(getString(R.string.phone))
        row0.createCell(2).setCellValue(getString(R.string.address))
        row0.createCell(3).setCellValue(getString(R.string.e_mail))

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
        val sheet = workbook.createSheet(getString(R.string.clients_label))
        var numberOfRows = 0
        val row0 = sheet.createRow(numberOfRows)
        row0.createCell(0).setCellValue(getString(R.string.name))
        row0.createCell(1).setCellValue(getString(R.string.phone))
        row0.createCell(2).setCellValue(getString(R.string.address))
        row0.createCell(3).setCellValue(getString(R.string.e_mail))

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
        val sheet = workbook.createSheet(getString(R.string.products_label))
        var numberOfRows = 0
        val row0 = sheet.createRow(numberOfRows)
        row0.createCell(0).setCellValue(getString(R.string.name))
        row0.createCell(1).setCellValue(getString(R.string.stock))
        row0.createCell(2).setCellValue(getString(R.string.barcode))
        row0.createCell(3).setCellValue(getString(R.string.buy_price))
        row0.createCell(4).setCellValue(getString(R.string.sell_price))
        row0.createCell(5).setCellValue(getString(R.string.suggested_sell_price))
        row0.createCell(6).setCellValue(getString(R.string.sku))
        row0.createCell(7).setCellValue(getString(R.string.brand))
        row0.createCell(8).setCellValue(getString(R.string.size))

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
                            row.createCell(6).setCellValue(it.sku)
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