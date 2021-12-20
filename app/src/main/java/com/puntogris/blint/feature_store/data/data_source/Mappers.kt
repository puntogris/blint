package com.puntogris.blint.feature_store.data.data_source

import com.google.firebase.auth.FirebaseUser
import com.puntogris.blint.common.utils.Constants
import com.puntogris.blint.feature_store.data.data_source.remote.FirestoreUser
import com.puntogris.blint.feature_store.domain.model.AuthUser
import com.puntogris.blint.feature_store.domain.model.Store
import com.puntogris.blint.feature_store.domain.model.User
import com.puntogris.blint.feature_store.domain.model.excel.ProductRecord
import com.puntogris.blint.feature_store.domain.model.excel.ProductRecordExcel
import com.puntogris.blint.feature_store.domain.model.excel.TraderRecordExcel
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

fun AuthUser.toFirestoreUser(): FirestoreUser {
    return FirestoreUser(
        uid = uid,
        name = name,
        photoUrl = photoUrl,
        email = email
    )
}

fun Product.toInitialRecord(currentBusinessId: String): Record {
    return Record(
        type = Constants.INITIAL,
        amount = stock,
        productId = productId,
        productName = name,
        storeId = currentBusinessId,
        barcode = barcode,
        historicInStock = stock,
        sku = sku,
    )
}

fun ProductWithDetails.toProductSupplierCrossRef(): List<ProductSupplierCrossRef> {
    return traders.map { ProductSupplierCrossRef(product.productId, it.traderId) }
}

fun ProductWithDetails.toProductCategoryCrossRef(): List<ProductCategoryCrossRef> {
    return categories.map { ProductCategoryCrossRef(product.productId, it.categoryName) }
}

fun NewOrder.toOrderWithRecords(store: Store): OrderWithRecords {
    return OrderWithRecords(
        order = Order(
            orderId = orderId,
            number = store.totalOrders + 1,
            total = total,
            type = type,
            traderName = traderName,
            traderId = traderId,
            storeId = store.storeId,
            businessName = store.name,
            debtId = newDebt?.debtId ?: ""
        ),
        records = newRecords.map {
            Record(
                recordId = it.recordId,
                orderId = orderId,
                type = type,
                traderId = traderId,
                traderName = traderName,
                productImage = it.productImage,
                amount = it.amount,
                productName = it.productName,
                productId = it.productId,
                historicOutStock = if (type == Constants.OUT) it.historicalOutStock + it.amount else it.historicalOutStock,
                historicInStock = when (type) {
                    Constants.IN -> it.historicalInStock + it.amount
                    Constants.INITIAL -> it.historicalInStock + it.amount
                    else -> it.historicalInStock
                },
                storeId = store.storeId,
                barcode = it.productBarcode,
                productUnitPrice = it.productUnitPrice,
                sku = it.productSku,
            )
        },
        debt = newDebt?.let {
            Debt(
                orderId = orderId,
                amount = it.amount,
                traderName = traderName,
                traderId = traderId,
                traderType = if (type == Constants.IN) Constants.SUPPLIER else Constants.CLIENT,
                storeId = store.storeId
            )
        }
    )
}

fun Product.toNewRecord(): NewRecord {
    return NewRecord(
        productName = name,
        productId = productId,
        productBarcode = barcode,
        productSku = sku,
        historicalInStock = historicInStock,
        historicalOutStock = historicOutStock,
        amount = 0,
        productUnitPrice = buyPrice,
        currentStock = stock
    )
}

fun Record.toProductRecordExcel(product: Product): ProductRecordExcel {
    return ProductRecordExcel(
        product.name,
        if (type == Constants.IN || type == Constants.INITIAL) {
            product.historicInStock - (historicInStock - amount).absoluteValue
        } else {
            product.historicInStock - historicInStock
        },
        if (type == Constants.OUT) {
            product.historicOutStock - (historicOutStock - amount).absoluteValue
        } else {
            product.historicOutStock - historicOutStock
        }
    )
}

fun List<Record>.toTraderExcelList(): List<TraderRecordExcel> {
    return groupBy {
        it.traderName
    }.map { supplierGroup ->
        TraderRecordExcel(
            traderName = supplierGroup.key,
            products = supplierGroup.value.groupBy { it.productName }.map { productGroup ->
                ProductRecord(
                    productName = productGroup.key,
                    amount = productGroup.value.sumOf { it.amount }
                )
            }
        )
    }
}