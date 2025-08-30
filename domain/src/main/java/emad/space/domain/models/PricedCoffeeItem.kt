package emad.space.domain.models

import java.math.BigDecimal

data class PricedCoffeeItem(
    val item: CoffeeItem,
    val category: CoffeeCategory,
    val price: BigDecimal
)