package com.puntogris.blint.common.framework

import android.content.Context
import android.net.Uri
import com.puntogris.blint.R
import com.puntogris.blint.feature_store.domain.model.Client
import com.puntogris.blint.feature_store.domain.model.Supplier
import com.puntogris.blint.feature_store.domain.model.excel.ProductRecordExcel
import com.puntogris.blint.feature_store.domain.model.excel.TraderRecordExcel
import com.puntogris.blint.feature_store.domain.model.product.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.poi.xssf.usermodel.XSSFWorkbook

class ExcelDrawer(private val context: Context, private val workbook: XSSFWorkbook) {

    suspend fun drawClientList(clients: List<Client>, uri: Uri) {
        val sheet = workbook.createSheet(context.getString(R.string.clients_label))
        val row0 = sheet.createRow(0)
        row0.createCell(0).setCellValue(context.getString(R.string.name))
        row0.createCell(1).setCellValue(context.getString(R.string.phone))
        row0.createCell(2).setCellValue(context.getString(R.string.address))
        row0.createCell(3).setCellValue(context.getString(R.string.e_mail))

        clients.forEachIndexed { index, client ->
            val row = sheet.createRow(index.inc())
            row.createCell(0).setCellValue(client.name)
            row.createCell(1).setCellValue(client.phone)
            row.createCell(2).setCellValue(client.address)
            row.createCell(3).setCellValue(client.email)
        }
        context.writeToFile(uri, workbook)
        workbook.removeSheetAt(workbook.getSheetIndex(sheet.sheetName))
    }

    suspend fun drawSupplierList(suppliers: List<Supplier>, uri: Uri) {
        val sheet = workbook.createSheet(context.getString(R.string.suppliers_label))
        val row0 = sheet.createRow(0)
        row0.createCell(0).setCellValue(context.getString(R.string.name))
        row0.createCell(1).setCellValue(context.getString(R.string.phone))
        row0.createCell(2).setCellValue(context.getString(R.string.address))
        row0.createCell(3).setCellValue(context.getString(R.string.e_mail))

        suppliers.forEachIndexed { index, supplier ->
            val row = sheet.createRow(index.inc())
            row.createCell(0).setCellValue(supplier.companyName)
            row.createCell(1).setCellValue(supplier.companyPhone)
            row.createCell(2).setCellValue(supplier.address)
            row.createCell(3).setCellValue(supplier.companyEmail)
        }
        context.writeToFile(uri, workbook)
        workbook.removeSheetAt(workbook.getSheetIndex(sheet.sheetName))
    }

    suspend fun drawProductsList(products: List<Product>, uri: Uri) {
        val sheet = workbook.createSheet(context.getString(R.string.products_label))
        val row0 = sheet.createRow(0)
        row0.createCell(0).setCellValue(context.getString(R.string.name))
        row0.createCell(1).setCellValue(context.getString(R.string.stock))
        row0.createCell(2).setCellValue(context.getString(R.string.barcode))
        row0.createCell(3).setCellValue(context.getString(R.string.buy_price))
        row0.createCell(4).setCellValue(context.getString(R.string.sell_price))
        row0.createCell(5).setCellValue(context.getString(R.string.suggested_sell_price))
        row0.createCell(6).setCellValue(context.getString(R.string.sku))
        row0.createCell(7).setCellValue(context.getString(R.string.brand))
        row0.createCell(8).setCellValue(context.getString(R.string.size))

        products.forEachIndexed { index, product ->
            val row = sheet.createRow(index.inc())
            row.createCell(0).setCellValue(product.name)
            row.createCell(1).setCellValue(product.amount.toString())
            row.createCell(2).setCellValue(product.barcode)
            row.createCell(3).setCellValue(product.buyPrice.toString())
            row.createCell(4).setCellValue(product.sellPrice.toString())
            row.createCell(5).setCellValue(product.suggestedSellPrice.toString())
            row.createCell(6).setCellValue(product.sku)
            row.createCell(7).setCellValue(product.brand)
            row.createCell(8).setCellValue(product.size)
        }
        context.writeToFile(uri, workbook)
        workbook.removeSheetAt(workbook.getSheetIndex(sheet.sheetName))
    }

    suspend fun drawProductRecords(records: List<ProductRecordExcel>, uri: Uri) {
        val sheet = workbook.createSheet(context.getString(R.string.products_label))
        val row0 = sheet.createRow(0)
        row0.createCell(0).setCellValue(context.getString(R.string.product))
        row0.createCell(1).setCellValue(context.getString(R.string.in_entry))
        row0.createCell(2).setCellValue(context.getString(R.string.out_entry))

        records.sortedBy { it.name }.forEachIndexed { index, record ->
            val newRow = sheet.createRow(index + 1)
            newRow.createCell(0).setCellValue(record.name)
            newRow.createCell(1).setCellValue(record.historicInStock.toString())
            newRow.createCell(2).setCellValue(record.historicOutStock.toString())
        }
        context.writeToFile(uri, workbook)
        workbook.removeSheetAt(workbook.getSheetIndex(sheet.sheetName))
    }

    suspend fun drawSupplierRecords(records: List<TraderRecordExcel>, uri: Uri) {
        val sheet = workbook.createSheet(context.getString(R.string.suppliers_label))
        val row0 = sheet.createRow(0)
        var numberOfRows = 0

        row0.createCell(0).setCellValue(context.getString(R.string.supplier_label))
        row0.createCell(1).setCellValue(context.getString(R.string.product_label))
        row0.createCell(2).setCellValue(context.getString(R.string.in_entry))

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
        workbook.removeSheetAt(workbook.getSheetIndex(sheet.sheetName))
    }

    suspend fun drawClientsRecords(records: List<TraderRecordExcel>, uri: Uri) {
        val sheet = workbook.createSheet(context.getString(R.string.clients_label))
        val row0 = sheet.createRow(0)
        var numberOfRows = 0
        row0.createCell(0).setCellValue(context.getString(R.string.client_label))
        row0.createCell(1).setCellValue(context.getString(R.string.product_label))
        row0.createCell(2).setCellValue(context.getString(R.string.out_entry))

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
        workbook.removeSheetAt(workbook.getSheetIndex(sheet.sheetName))
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