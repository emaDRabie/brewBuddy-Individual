package emad.space.domain.repo

import emad.space.domain.models.CoffeeCategory
import emad.space.domain.models.CoffeeItem
import java.math.BigDecimal

interface PricingRepo {
    fun getPriceFor(item: CoffeeItem, category: CoffeeCategory): BigDecimal
}