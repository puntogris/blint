package com.puntogris.blint.common.framework

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract.*
import android.provider.ContactsContract.CommonDataKinds.*
import com.puntogris.blint.feature_store.domain.model.Trader
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@SuppressLint("Range")
class ContactsHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun getSaveContactIntent(trader: Trader): Intent {
        return Intent(Intent.ACTION_INSERT).apply {
            type = Contacts.CONTENT_TYPE
            putExtra(Intents.Insert.NAME, trader.name)
            putExtra(Intents.Insert.PHONE, trader.phone)
            putExtra(Intents.Insert.EMAIL, trader.email)
            putExtra(Intents.Insert.POSTAL, trader.address)
        }
    }

    fun getContact(uri: Uri): Contact? {
        var contact: Contact? = null

        context.contentResolver.query(
            uri,
            arrayOf(Data.LOOKUP_KEY),
            null,
            null,
            null
        )?.let { cursor ->
            if (cursor.moveToFirst()) {
                val lookUpKey = cursor.getString(cursor.getColumnIndex(Data.LOOKUP_KEY))
                if (lookUpKey != null) {
                    val email = getEmailWithLookUpKey(lookUpKey)
                    val phone = getPhoneAndNameWithLookUpKey(lookUpKey)
                    val address = loadAddressWithLookUpKey(lookUpKey)
                    contact = Contact(
                        email = email,
                        address = address,
                        phone = phone["phone"].toString(),
                        name = phone["name"].toString()
                    )
                }
            }
            cursor.close()
        }
        return contact
    }

    private fun getEmailWithLookUpKey(key: String): String {
        val selection = Data.LOOKUP_KEY + " = ? AND " + Data.MIMETYPE + " = ?"
        val args = arrayOf(key, Email.CONTENT_ITEM_TYPE)
        return context.contentResolver.query(
            Data.CONTENT_URI,
            null,
            selection,
            args,
            null
        )?.let {
            val email = if (it.moveToNext()) it.getString(it.getColumnIndex(Email.DATA)) else ""
            it.close()
            email
        } ?: ""
    }

    private fun loadAddressWithLookUpKey(key: String): String {
        val selection = Data.LOOKUP_KEY + " = ? AND " + Data.MIMETYPE + " = ?"
        val args = arrayOf(key, StructuredPostal.CONTENT_ITEM_TYPE)
        val cursor = context.contentResolver.query(
            Data.CONTENT_URI,
            null,
            selection,
            args,
            null
        )
        val address = if (cursor!!.moveToNext()) {
            cursor.getString(cursor.getColumnIndex(StructuredPostal.FORMATTED_ADDRESS))
        } else ""
        cursor.close()
        return address
    }

    private fun getPhoneAndNameWithLookUpKey(key: String): Map<String, String> {
        val selection = Data.LOOKUP_KEY + " = ? AND " + Data.MIMETYPE + " = ?"
        val args = arrayOf(key, Phone.CONTENT_ITEM_TYPE)
        val cursor = context.contentResolver.query(
            Data.CONTENT_URI,
            null,
            selection,
            args,
            null
        )
        val phone = hashMapOf<String, String>()
        if (cursor != null && cursor.count > 0 &&
            cursor.moveToNext() &&
            cursor.getString(cursor.getColumnIndex(Contacts.HAS_PHONE_NUMBER)).toInt() > 0
        ) {
            phone["name"] = cursor.getString(cursor.getColumnIndex(Contacts.DISPLAY_NAME))
            phone["phone"] = cursor.getString(cursor.getColumnIndex(Phone.NUMBER))
        }

        cursor?.close()
        return phone
    }
}

data class Contact(
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val address: String = ""
)
