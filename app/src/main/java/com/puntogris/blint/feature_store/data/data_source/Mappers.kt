package com.puntogris.blint.feature_store.data.data_source

import com.google.firebase.auth.FirebaseUser
import com.puntogris.blint.common.utils.Constants
import com.puntogris.blint.feature_store.domain.model.AuthUser
import com.puntogris.blint.feature_store.domain.model.Business
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

fun Product.toRecord(currentBusinessId: String): Record {
    return Record(
        type = Constants.INITIAL,
        amount = amount,
        productId = productId,
        productName = name,
        businessId = currentBusinessId,
        barcode = barcode,
        historicInStock = amount,
        sku = sku,
    )
}

fun ProductWithDetails.toProductSupplierCrossRef(): List<ProductSupplierCrossRef> {
    return suppliers.map { ProductSupplierCrossRef(product.productId, it.supplierId) }
}

fun ProductWithDetails.toProductCategoryCrossRef(): List<ProductCategoryCrossRef> {
    return categories.map { ProductCategoryCrossRef(product.productId, it.categoryName) }
}

fun NewOrder.toOrderWithRecords(business: Business): OrderWithRecords {
    return OrderWithRecords(
        order = Order(
            orderId = orderId,
            number = business.totalOrders + 1,
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
                historicOutStock = it.historicalOutStock,
                historicInStock = it.historicalInStock,
                businessId = business.businessId,
                barcode = it.barcode,
                productUnitPrice = it.productUnitPrice,
                sku = it.sku,
            ).also { record ->
                if (record.type == Constants.IN) record.historicInStock += record.amount.absoluteValue
                else record.historicOutStock += record.amount.absoluteValue
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

fun Record.toProductRecordExcel(product: Product): ProductRecordExcel {
    return ProductRecordExcel(
        product.name,
        if (type == "IN") {
            product.historicInStock - (historicInStock - amount).absoluteValue
        } else {
            product.historicInStock - historicInStock
        },
        if (type == "OUT") {
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