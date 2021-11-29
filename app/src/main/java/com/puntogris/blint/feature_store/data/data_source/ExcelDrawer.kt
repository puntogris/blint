package com.puntogris.blint.feature_store.data.data_source

import android.content.Context
import android.net.Uri
import com.puntogris.blint.R
import com.puntogris.blint.feature_store.domain.model.Client
import com.puntogris.blint.feature_store.domain.model.excel.TraderRecordExcel
import com.puntogris.blint.feature_store.domain.model.Supplier
import com.puntogris.blint.feature_store.domain.model.product.Product
import com.puntogris.blint.feature_store.domain.model.excel.ProductRecordExcel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.poi.xssf.usermodel.XSSFWorkbook

class ExcelDrawer(private val context: Context, private val workbook: XSSFWorkbook) {

    suspend fun drawClientList(clients: List<Client>, uri: Uri) {
        val sheet = workbook.createSheet(context.getString(R.string.clients_label))
        var numberOfRows = 0
        val row0 = sheet.createRow(numberOfRows)
        row0.createCell(0).setCellValue(context.getString(R.string.name))
        row0.createCell(1).setCellValue(context.getString(R.string.phone))
        row0.createCell(2).setCellValue(context.getString(R.string.address))
        row0.createCell(3).setCellValue(context.getString(R.string.e_mail))

        clients.forEach {
            numberOfRows += 1
            val row = sheet.createRow(numberOfRows)
            row.createCell(0).setCellValue(it.name)
            row.createCell(1).setCellValue(it.phone)
            row.createCell(2).setCellValue(it.address)
            row.createCell(3).setCellValue(it.email)
        }
        context.writeToFile(uri, workbook)
    }

    suspend fun drawSupplierList(suppliers: List<Supplier>, uri: Uri) {
        val sheet = workbook.createSheet(context.getString(R.string.suppliers_label))
        var numberOfRows = 0
        val row0 = sheet.createRow(numberOfRows)
        row0.createCell(0).setCellValue(context.getString(R.string.name))
        row0.createCell(1).setCellValue(context.getString(R.string.phone))
        row0.createCell(2).setCellValue(context.getString(R.string.address))
        row0.createCell(3).setCellValue(context.getString(R.string.e_mail))

        suppliers.forEach {
            numberOfRows += 1
            val row = sheet.createRow(numberOfRows)
            row.createCell(0).setCellValue(it.companyName)
            row.createCell(1).setCellValue(it.companyPhone)
            row.createCell(2).setCellValue(it.address)
            row.createCell(3).setCellValue(it.companyEmail)
        }
        context.writeToFile(uri, workbook)
    }

    suspend fun drawProductsList(products: List<Product>, uri: Uri) {
        val sheet = workbook.createSheet(context.getString(R.string.products_label))
        var numberOfRows = 0
        val row0 = sheet.createRow(numberOfRows)
        row0.createCell(0).setCellValue(context.getString(R.string.name))
        row0.createCell(1).setCellValue(context.getString(R.string.stock))
        row0.createCell(2).setCellValue(context.getString(R.string.barcode))
        row0.createCell(3).setCellValue(context.getString(R.string.buy_price))
        row0.createCell(4).setCellValue(context.getString(R.string.sell_price))
        row0.createCell(5).setCellValue(context.getString(R.string.suggested_sell_price))
        row0.createCell(6).setCellValue(context.getString(R.string.sku))
        row0.createCell(7).setCellValue(context.getString(R.string.brand))
        row0.createCell(8).setCellValue(context.getString(R.string.size))

        products.forEach {
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
        context.writeToFile(uri, workbook)
    }

    suspend fun drawProductRecords(records: List<ProductRecordExcel>, uri: Uri) {
        val sheet = workbook.createSheet(context.getString(R.string.products_label))
        val row0 = sheet.createRow(0)
        row0.createCell(0).setCellValue(context.getString(R.string.product))
        row0.createCell(1).setCellValue(context.getString(R.string.in_entry))
        row0.createCell(2).setCellValue(context.getString(R.string.out_entry))
        var numberOfRows = 0

        records.sortedBy { it.name }
        records.forEach {

            numberOfRows += 1
            val newRow = sheet.createRow(numberOfRows)
            newRow.createCell(0).setCellValue(it.name)
            newRow.createCell(1).setCellValue(it.historicInStock.toString())
            newRow.createCell(2).setCellValue(it.historicOutStock.toString())
        }
        context.writeToFile(uri, workbook)
    }

    suspend fun drawSupplierRecords(records: List<TraderRecordExcel>, uri: Uri) {
        val sheet = workbook.createSheet("Supplier")
        val row0 = sheet.createRow(0)
        row0.createCell(0).setCellValue("Supplier")
        row0.createCell(1).setCellValue("Product")
        row0.createCell(2).setCellValue("In")
        var numberOfRows = 0

        records.sortedBy { it.traderName }.forEach { supplier ->
            numberOfRows += 1
            val newRow = sheet.createRow(numberOfRows)
            newRow.createCell(0).setCellValue(supplier.traderName)
            supplier.products.forEach {
                numberOfRows += 1
                val productRow = sheet.createRow(numberOfRows)
                productRow.createCell(1).setCellValue(it.productName)
                productRow.createCell(2).setCellValue(it.amount.toString())
            }
        }

        context.writeToFile(uri, workbook)
    }

    suspend fun drawClientsRecords(records: List<TraderRecordExcel>, uri: Uri){
        val sheet = workbook.createSheet("Clients")
        val row0 = sheet.createRow(0)
        row0.createCell(0).setCellValue("Client")
        row0.createCell(1).setCellValue("Product")
        row0.createCell(2).setCellValue("Out")
        var numberOfRows = 0

        records.sortedBy { it.traderName }.forEach { client ->
            numberOfRows += 1
            val newRow = sheet.createRow(numberOfRows)
            newRow.createCell(0).setCellValue(client.traderName)
            client.products.forEach {
                numberOfRows += 1
                val productRow = sheet.createRow(numberOfRows)
                productRow.createCell(1).setCellValue(it.productName)
                productRow.createCell(2).setCellValue(it.amount.toString())
            }
        }
        context.writeToFile(uri, workbook)
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    private suspend fun Context.writeToFile(uri: Uri, workbook: XSSFWorkbook) {
        withContext(Dispatchers.IO) {
            val file = contentResolver.openOutputStream(uri)
            workbook.write(file)
            file?.close()
        }
    }
}