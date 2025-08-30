package emad.space.domain.usecases

import emad.space.domain.models.Order
import emad.space.domain.repo.OrdersRepo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PlaceOrderUseCase @Inject constructor(
    private val repo: OrdersRepo
) {
    suspend operator fun invoke(order: Order): String = repo.placeOrder(order)
}

class ObserveOrdersUseCase @Inject constructor(
    private val repo: OrdersRepo
) {
    operator fun invoke(): Flow<List<Order>> = repo.observeOrders()
}