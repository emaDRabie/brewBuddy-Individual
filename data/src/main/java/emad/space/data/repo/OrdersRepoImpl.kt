package emad.space.data.repo

import emad.space.data.local.dao.OrdersDao
import emad.space.data.local.entities.OrderEntity
import emad.space.data.local.entities.OrderItemEntity
import emad.space.data.local.entities.OrderWithItems
import emad.space.domain.models.CoffeeCategory
import emad.space.domain.models.Order
import emad.space.domain.models.OrderItem
import emad.space.domain.models.PricedCoffeeItem
import emad.space.domain.repo.OrdersRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.math.BigDecimal
import java.util.UUID

class OrdersRepoImpl(
    private val dao: OrdersDao
) : OrdersRepo {

    override suspend fun placeOrder(order: Order): String {
        val id = order.id.ifBlank { UUID.randomUUID().toString() }
        val orderEntity = OrderEntity(id = id, placedAtMillis = order.placedAtEpochMillis)
        val items = order.items.map { it.toEntity(id) }
        dao.insertOrder(orderEntity)
        dao.insertItems(items)
        return id
    }

    override fun observeOrders(): Flow<List<Order>> =
        dao.observeOrders().map { list -> list.map { it.toDomain() } }

    override suspend fun getOrderById(id: String): Order? =
        dao.getOrderById(id)?.toDomain()

    private fun OrderItem.toEntity(orderId: String) = OrderItemEntity(
        orderId = orderId,
        coffeeId = coffee.item.id ?: 0,
        title = coffee.item.title.orEmpty(),
        image = coffee.item.image,
        category = coffee.category.name,
        quantity = quantity,
        priceCents = coffee.price.movePointRight(2).toLong()
    )

    private fun OrderWithItems.toDomain(): Order {
        val items = items.map {
            val price = BigDecimal(it.priceCents).movePointLeft(2)
            val pItem = PricedCoffeeItem(
                item = emad.space.domain.models.CoffeeItem(
                    title = it.title,
                    description = null,
                    ingredients = null,
                    image = it.image,
                    id = it.coffeeId
                ),
                category = CoffeeCategory.valueOf(it.category),
                price = price
            )
            OrderItem(coffee = pItem, quantity = it.quantity)
        }
        return Order(
            id = order.id,
            items = items,
            placedAtEpochMillis = order.placedAtMillis
        )
    }
}