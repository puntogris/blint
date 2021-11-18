package com.puntogris.blint.data.data_source

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser
import com.puntogris.blint.model.*
import com.puntogris.blint.utils.Constants

fun FirebaseUser.toAuthUser(): AuthUser {
    return AuthUser(
        name = requireNotNull(displayName),
        uid = uid,
        photoUrl = requireNotNull(photoUrl).toString(),
        email = requireNotNull(email)
    )
}

fun AuthUser.toUserEntity(): User {
    return User(
        uid = uid,
        name = name,
        photoUrl = photoUrl,
        email = email
    )
}

fun Product.toRecord(currentBusinessId: Int): Record{
    return Record(
        type = Constants.INITIAL,
        amount = amount,
        productId = productId,
        productName = name,
        timestamp = Timestamp.now(),
        businessId = currentBusinessId,
        barcode = barcode,
        totalInStock = amount,
        sku = sku,
    )
}

fun ProductWithDetails.toProductSupplierCrossRef(): List<ProductSupplierCrossRef>{
    return suppliers.map { ProductSupplierCrossRef(product.productId, it.supplierId) }
}

fun ProductWithDetails.toProductCategoryCrossRef(): List<ProductCategoryCrossRef>{
    return categories.map { ProductCategoryCrossRef(product.productId, it.categoryName) }
}