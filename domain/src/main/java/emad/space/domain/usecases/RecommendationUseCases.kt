package emad.space.domain.usecases

import emad.space.domain.models.CoffeeItem
import javax.inject.Inject
import kotlin.random.Random

class GetBestSellerUseCase @Inject constructor() {
    operator fun invoke(items: List<CoffeeItem>): CoffeeItem? {
        if (items.isEmpty()) return null
        // Placeholder: pick a random "best seller" if there is no rating field
        return items[Random.nextInt(items.size)]
    }
}

class GetWeeklyRecommendationsUseCase @Inject constructor() {
    operator fun invoke(items: List<CoffeeItem>, count: Int = 10): List<CoffeeItem> {
        if (items.isEmpty() || count <= 0) return emptyList()
        return items.shuffled().take(count)
    }
}