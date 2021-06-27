package com.puntogris.blint.ui.record

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.puntogris.blint.data.local.dao.*
import com.puntogris.blint.data.remote.UserRepository
import com.puntogris.blint.model.Order
import com.puntogris.blint.model.ProductWithRecord
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NewOrderViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val clientsDao: ClientsDao,
    private val suppliersDao: SuppliersDao,
    private val productsDao: ProductsDao,
    private val ordersDao: OrdersDao,
    private val userDao: UsersDao
): ViewModel() {

    private val order = Order()

    fun getOrder() = order

    fun updateRecordType(code: Int){
        order.type = when(code){
            0 -> "IN"
            else -> "OUT"
        }
    }
    fun getOrderType() = order.type

    fun updateOrderValue(newValue:Float){
        order.value = newValue
    }

    fun updateOrderExternalInfo(name:String, id:String){
        order.traderId = id
        order.traderName = name
    }

    fun updateOrdersItems(items:List<ProductWithRecord>){
        order.items = items.map {
            it.record
        }
    }

    suspend fun getClientsWithName(name: String) = clientsDao.getClientWithName(name)

    suspend fun getSuppliersWithName(name: String) = suppliersDao.getSupplierWithName(name)

    suspend fun getProductWithName(name: String) = productsDao.getProductsWithName(name)

    fun getCurrentUserEmail() =
        userRepository.getCurrentUser()?.email

    suspend fun saveOrderIntoDatabase(){
        val businessId = userDao.getUser().currentBusinessId
        order.businessId = businessId
        val orderId = ordersDao.insert(order)
        order.items.forEach {
            it.type = order.type
            it.author = order.author
            it.businessId = businessId
            it.traderName = order.traderName
            it.traderId = order.traderId
            it.orderId = orderId.toString()
            ordersDao.insert(it)
            productsDao.updateProductAmountWithType(it.productId, it.amount, it.type)
        }
    }

}
