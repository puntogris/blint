package com.puntogris.blint.common.utils.types


sealed class SearchText {
    class QrCode(val text: String) : SearchText()
    class Name(val text: String) : SearchText()
    class InternalCode(val text: String) : SearchText()
    class Category(val text: String) : SearchText()

    fun getData(): String {
        return when (this) {
            is InternalCode -> text
            is Name -> text
            is QrCode -> text
            is Category -> text
        }
    }
}