package emad.space.domain.repo

import emad.space.domain.models.Order
import kotlinx.coroutines.flow.Flow

interface OrdersRepo {
    suspend fun placeOrder(order: Order): String
    fun observeOrders(): Flow<List<Order>>
    suspend fun getOrderById(id: String): Order?
}