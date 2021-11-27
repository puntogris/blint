package com.puntogris.blint.feature_store.data.data_source

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser
import com.puntogris.blint.common.utils.Constants
import com.puntogris.blint.feature_store.domain.model.AuthUser
import com.puntogris.blint.feature_store.domain.model.Business
import com.puntogris.blint.feature_store.domain.model.Statistic
import com.puntogris.blint.feature_store.domain.model.User
import com.puntogris.blint.feature_store.domain.model.order.*
import com.puntogris.blint.feature_store.domain.model.product.Product
import com.puntogris.blint.feature_store.domain.model.product.ProductCategoryCrossRef
import com.puntogris.blint.feature_store.domain.model.product.ProductSupplierCrossRef
import com.puntogris.blint.feature_store.domain.model.product.ProductWithDetails
import kotlin.math.absoluteValue

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

fun Product.toRecord(currentBusinessId: String): Record {
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

fun ProductWithDetails.toProductSupplierCrossRef(): List<ProductSupplierCrossRef> {
    return suppliers.map { ProductSupplierCrossRef(product.productId, it.supplierId) }
}

fun ProductWithDetails.toProductCategoryCrossRef(): List<ProductCategoryCrossRef> {
    return categories.map { ProductCategoryCrossRef(product.productId, it.categoryName) }
}

fun NewOrder.toOrderWithRecords(business: Business, statistic: Statistic): OrderWithRecords {
    return OrderWithRecords(
        order = Order(
            orderId = orderId,
            number = statistic.totalOrders + 1,
            value = value,
            type = type,
            traderName = traderName,
            traderId = traderId,
            businessId = business.businessId,
            businessName = business.name,
            debtId = newDebt?.debtId ?: ""
        ),
        records = newRecords.map {
            Record(
                recordId = it.recordId,
                orderId = orderId,
                type = type,
                traderId = traderId,
                traderName = traderName,
                amount = it.amount,
                productName = it.productName,
                productId = it.productId,
                totalOutStock = it.historicalOutStock,
                totalInStock = it.historicalInStock,
                businessId = business.businessId,
                barcode = it.barcode,
                productUnitPrice = it.productUnitPrice,
                sku = it.sku,
            ).also { record ->
                if (record.type == Constants.IN) record.totalInStock += record.amount.absoluteValue
                else record.totalOutStock += record.amount.absoluteValue
            }
        },
        debt = newDebt?.let {
            Debt(
                orderId = orderId,
                amount = it.amount,
                traderName = traderName,
                traderId = traderId,
                traderType = if (type == Constants.IN) Constants.SUPPLIER else Constants.CLIENT,
                businessId = business.businessId
            )
        }
    )
}

fun Product.toNewRecord(): NewRecord {
    return NewRecord(
        productName = name,
        productId = productId,
        barcode = barcode,
        sku = sku,
        historicalInStock = historicInStock,
        historicalOutStock = historicOutStock,
        amount = 0,
        productUnitPrice = buyPrice,
        currentStock = amount
    )
}