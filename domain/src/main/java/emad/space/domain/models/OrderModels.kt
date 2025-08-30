package emad.space.domain.models

import java.math.BigDecimal

data class OrderItem(
    val coffee: PricedCoffeeItem,
    val quantity: Int
) {
    val lineTotal: BigDecimal get() = coffee.price.multiply(BigDecimal(quantity))
}

data class Order(
    val id: String,
    val items: List<OrderItem>,
    val placedAtEpochMillis: Long
) {
    val total: BigDecimal get() = items.fold(BigDecimal.ZERO) { acc, it -> acc + it.lineTotal }
}