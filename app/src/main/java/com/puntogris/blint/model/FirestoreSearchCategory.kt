package com.puntogris.blint.model

class FirestoreSearchCategory(
    var categoryId: String = "",
    var name: String = "",
    var businessId: String = "",
    val search_name: List<String>
)