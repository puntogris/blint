package com.puntogris.blint.model

class SupplierRecordExcel(
    val supplierName: String = "",
    val products: List<ProductRecord> = listOf()
)
class SuppliersRecords(val suppliers: List<SupplierRecordData> = listOf())

class ProductRecord(val productName:String = "", val amount:Int = 0)

class SupplierRecordData(
    val supplierId: String = "",
    val productsIds: List<String> = listOf()
)
