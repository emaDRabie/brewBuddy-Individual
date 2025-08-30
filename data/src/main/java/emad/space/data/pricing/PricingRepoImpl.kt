package emad.space.data.pricing

import emad.space.domain.models.CoffeeCategory
import emad.space.domain.models.CoffeeItem
import emad.space.domain.repo.PricingRepo
import java.math.BigDecimal

class PricingRepoImpl : PricingRepo {
    override fun getPriceFor(item: CoffeeItem, category: CoffeeCategory): BigDecimal {
        // Simple strategy: HOT base = 3.50, ICED base = 4.00, +0.25 per ingredient
        val base = if (category == CoffeeCategory.HOT) BigDecimal("3.50") else BigDecimal("4.00")
        val count = item.ingredients?.count { !it.isNullOrBlank() } ?: 0
        return base + BigDecimal("0.25") * BigDecimal(count)
    }
}