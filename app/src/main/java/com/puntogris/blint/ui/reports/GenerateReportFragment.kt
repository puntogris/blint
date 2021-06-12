package com.puntogris.blint.ui.reports

import android.app.Activity
import android.content.Intent
import android.net.Uri
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
import java.io.File

@AndroidEntryPoint
class GenerateReportFragment :
    BaseFragment<FragmentGenerateReportBinding>(R.layout.fragment_generate_report) {

    private val viewModel: ReportsViewModel by viewModels()
    private val args: GenerateReportFragmentArgs by navArgs()

    override fun initializeViews() {
        createIntent(args.reportCode)
        lifecycleScope.launchWhenStarted {
            viewModel.exportingState.collect {
                when(it){
                    is ExportResult.Error -> showErrorUi()
                    ExportResult.InProgress -> showInProgressUi()
                    ExportResult.Success -> showSuccessUi()
                }
            }
        }
        getParentFab().apply {
            changeIconFromDrawable(R.drawable.ic_baseline_share_24)
            setOnClickListener {
                val uri = viewModel.getDownloadUri()
                if (uri == null)
                    showLongSnackBarAboveFab("Todavia no se genero el informe o ocurrio un error.")
                else shareFileIntent(uri)
            }
        }
    }

    private fun showInProgressUi(){
    }

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

    private fun exportSupplierRecords(downloadFileUri: Uri) {
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Supplier")
        val row0 = sheet.createRow(0)
        row0.createCell(0).setCellValue("Supplier")
        row0.createCell(1).setCellValue("Product")
        row0.createCell(2).setCellValue("In")
        var numberOfRows = 0
        var lastSupplierId = 0
        val recordExcel = mutableListOf<SupplierRecordExcel>()

        lifecycleScope.launch {
            try {
                val records =
                    viewModel.getSuppliersRecords(args.timeCode, args.startTime, args.endTime)

                records.forEach {
                    if (it.traderId != lastSupplierId) {
                        recordExcel.add(
                            SupplierRecordExcel(
                                it.productName,
                                it.traderName,
                                it.amount,
                                it.productId,
                                it.traderId
                            )
                        )
                    } else {
                        if (recordExcel.last().productId == it.productId) recordExcel.last().amount += it.amount
                        else recordExcel.add(
                            SupplierRecordExcel(
                                it.productName,
                                it.traderName,
                                it.amount,
                                it.productId,
                                it.traderId
                            )
                        )
                    }
                    lastSupplierId = it.traderId
                }

                recordExcel.sortBy { it.supplierName }

                lastSupplierId = 0
                recordExcel.forEach {
                    if (it.clientId != lastSupplierId) {
                        numberOfRows += 1
                        val newRow = sheet.createRow(numberOfRows)
                        newRow.createCell(0).setCellValue(it.supplierName)
                    }
                    numberOfRows += 1
                    val productRow = sheet.createRow(numberOfRows)
                    productRow.createCell(1).setCellValue(it.productName)
                    productRow.createCell(2).setCellValue(it.amount.toString())

                    lastSupplierId = it.clientId
                }

                withContext(Dispatchers.IO) {
                    val file = requireActivity().contentResolver.openOutputStream(downloadFileUri)
                    workbook.write(file)
                    file?.close()
                }
                viewModel.updateExportState(ExportResult.Success)
            } catch (e: Exception) {
                viewModel.updateExportState(ExportResult.Error(e))
            }
        }
    }

    private fun exportClientRecords(downloadFileUri: Uri) {
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Clients")
        val row0 = sheet.createRow(0)
        row0.createCell(0).setCellValue("Client")
        row0.createCell(1).setCellValue("Product")
        row0.createCell(2).setCellValue("Out")
        var numberOfRows = 0
        var lastClientId = 0
        val recordExcel = mutableListOf<ClientRecordExcel>()

        lifecycleScope.launch {
            try {
                val records = viewModel.getClientRecords(args.timeCode, args.startTime, args.endTime)

                records.forEach {
                    if (it.traderId != lastClientId) {
                        recordExcel.add(
                            ClientRecordExcel(
                                it.productName,
                                it.traderName,
                                it.amount,
                                it.productId,
                                it.traderId
                            )
                        )
                    } else {
                        if (recordExcel.last().productId == it.productId) recordExcel.last().amount += it.amount
                        else recordExcel.add(
                            ClientRecordExcel(
                                it.productName,
                                it.traderName,
                                it.amount,
                                it.productId,
                                it.traderId
                            )
                        )
                    }
                    lastClientId = it.traderId
                }

                recordExcel.sortBy { it.clientName }

                lastClientId = 0
                recordExcel.forEach {
                    if (it.clientId != lastClientId) {
                        numberOfRows += 1
                        val newRow = sheet.createRow(numberOfRows)
                        newRow.createCell(0).setCellValue(it.clientName)
                    }
                    numberOfRows += 1
                    val productRow = sheet.createRow(numberOfRows)
                    productRow.createCell(1).setCellValue(it.productName)
                    productRow.createCell(2).setCellValue(it.amount.toString())

                    lastClientId = it.clientId
                }

                withContext(Dispatchers.IO) {
                    val file = requireActivity().contentResolver.openOutputStream(downloadFileUri)
                    workbook.write(file)
                    file?.close()
                }
                viewModel.updateExportState(ExportResult.Success)
            }catch (e: Exception) {
                viewModel.updateExportState(ExportResult.Error(e))
            }

        }
    }

    private fun exportProductRecords(downloadFileUri: Uri) {
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Products")
        val row0 = sheet.createRow(0)
        row0.createCell(0).setCellValue("Product")
        row0.createCell(1).setCellValue("In")
        row0.createCell(2).setCellValue("Out")
        var numberOfRows = 0
        var lastProductId = 0
        val recordExcel = mutableListOf<ProductRecordExcel>()

        lifecycleScope.launch {
            try {
                val records = viewModel.getProductRecords(args.timeCode, args.startTime, args.endTime)

                records.forEach {
                    if (it.productId != lastProductId) {
                        if (it.type == "IN") recordExcel.add(
                            ProductRecordExcel(
                                it.productName,
                                it.amount,
                                0
                            )
                        )
                        else recordExcel.add(ProductRecordExcel(it.productName, 0, it.amount))
                    } else {
                        if (it.type == "IN") recordExcel.last().inAmount += it.amount
                        else recordExcel.last().outAmount += it.amount
                    }
                    lastProductId = it.productId
                }

                recordExcel.sortBy { it.name }

                recordExcel.forEach {
                    numberOfRows += 1
                    val newRow = sheet.createRow(numberOfRows)
                    newRow.createCell(0).setCellValue(it.name)
                    newRow.createCell(1).setCellValue(it.inAmount.toString())
                    newRow.createCell(2).setCellValue(it.outAmount.toString())
                }

                withContext(Dispatchers.IO) {
                    val file = requireActivity().contentResolver.openOutputStream(downloadFileUri)
                    workbook.write(file)
                    file?.close()
                }
                viewModel.updateExportState(ExportResult.Success)
            }catch (e: Exception) {
                viewModel.updateExportState(ExportResult.Error(e))
            }

        }
    }

    private fun shareFileIntent(uri: Uri) {
        val share = Intent(Intent.ACTION_SEND)
        share.type = "application/vnd.ms-excel"
        share.putExtra(Intent.EXTRA_STREAM, uri)
        startActivity(Intent.createChooser(share, "Compartir informe"))
    }

    private fun createIntent(code: Int) {
        val name = when (code) {
            SUPPLIERS_LIST -> "suppliers_list"
            CLIENTS_LIST -> "clients_list"
            PRODUCTS_LIST -> "products_list"
            SUPPLIERS_RECORDS -> "suppliers_records"
            CLIENTS_RECORDS -> "clients_records"
            else -> "products_records"
        }
        Intent(Intent.ACTION_CREATE_DOCUMENT).also {
            it.addCategory(Intent.CATEGORY_OPENABLE)
            it.type = "application/vnd.ms-excel"
            it.putExtra(Intent.EXTRA_TITLE, name.getDateWithFileName())
            startActivityForResult(it, code)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) return

        val downloadFileUri: Uri = data!!.data!!
        viewModel.saveDownloadUri(downloadFileUri)
        requireActivity().contentResolver.openOutputStream(downloadFileUri)

        when (requestCode) {
            SUPPLIERS_LIST -> exportSupplierList(downloadFileUri)
            CLIENTS_LIST -> exportClientList(downloadFileUri)
            PRODUCTS_LIST -> exportProductList(downloadFileUri)
            SUPPLIERS_RECORDS -> exportSupplierRecords(downloadFileUri)
            CLIENTS_RECORDS -> exportClientRecords(downloadFileUri)
            PRODUCTS_RECORDS -> exportProductRecords(downloadFileUri)
        }
    }

    private fun exportSupplierList(downloadFileUri: Uri) {

//        val sqliteToExcel = SQLiteToExcel(
//            requireContext(),
//            "blint_database",
//            requireContext().filesDir.path
//        )
//        val prettyNameMapping = hashMapOf<String, String>()
//        sqliteToExcel.setExcludeColumns(listOf("supplierId", "businessId"))
//        sqliteToExcel.exportSingleTable(
//            "Supplier", "supplier.xls", exportTableListener(
//                downloadFileUri
//            )
//        )
    }

    private fun exportClientList(downloadFileUri: Uri) {
//        val sqliteToExcel = SQLiteToExcel(
//            requireContext(),
//            "blint_database",
//            requireContext().filesDir.path
//        )
//        val prettyNameMapping = hashMapOf<String, String>()
//        sqliteToExcel.setExcludeColumns(listOf("clientId", "businessId"))
//        sqliteToExcel.exportSingleTable(
//            "Client",
//            "clients.xls",
//            exportTableListener(downloadFileUri)
//        )
    }


    private fun exportProductList(downloadFileUri: Uri) {
//        val sqliteToExcel = SQLiteToExcel(
//            requireContext(),
//            "blint_database",
//            requireContext().filesDir.path
//        )
//        val prettyNameMapping = hashMapOf<String, String>()
//        val columnsToExclude = listOf(
//            "productId",
//            "image",
//            "lastRecordTimestamp",
//            "suppliers",
//            "businessId",
//            "totalInStock",
//            "totalOutStock"
//        )
//        sqliteToExcel.setExcludeColumns(columnsToExclude)
//        sqliteToExcel.exportSingleTable(
//            "Product", "/productos.xls", exportTableListener(
//                downloadFileUri
//            )
//        )
    }

   // private fun exportTableListener(downloadFileUri: Uri): ExportListener {
//        return object : ExportListener {
//            override fun onStart() {}
//            override fun onCompleted(filePath: String) {
//                try {
//                    requireActivity().contentResolver.openInputStream(Uri.fromFile(File(filePath)))
//                        ?.use { inputStream ->
//                            requireActivity().contentResolver.openOutputStream(downloadFileUri)?.let {
//                                inputStream.copyTo(it)
//                                it.close()
//                            }
//                        }
//                    viewModel.updateExportState(ExportResult.Success)
//                }catch (e:Exception){
//                    viewModel.updateExportState(ExportResult.Error(e))
//                }
//            }
//            override fun onError(e: Exception) {
//                viewModel.updateExportState(ExportResult.Error(e))
//            }
//        }
 //   }

}