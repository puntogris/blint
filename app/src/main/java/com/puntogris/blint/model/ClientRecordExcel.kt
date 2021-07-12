package com.puntogris.blint.model

class ClientRecordExcel(
    val clientName: String = "",
    val products: List<ProductRecord> = listOf()
)
class ClientsRecords(val clients: List<ClientRecordData> = listOf())

class ClientRecordData(
    val clientId: String = "",
    val productsIds: List<String> = listOf())
