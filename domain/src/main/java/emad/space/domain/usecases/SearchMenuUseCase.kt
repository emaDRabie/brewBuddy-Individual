package emad.space.domain.usecases

import emad.space.domain.models.CoffeeItem
import java.util.Locale
import javax.inject.Inject

class SearchMenuUseCase @Inject constructor() {
    operator fun invoke(items: List<CoffeeItem>, query: String): List<CoffeeItem> {
        if (query.isBlank()) return items
        val q = query.lowercase(Locale.getDefault())
        return items.filter { item ->
            val title = item.title?.lowercase(Locale.getDefault()).orEmpty()
            val desc = item.description?.lowercase(Locale.getDefault()).orEmpty()
            val ingredients = item.ingredients?.joinToString(" ") { it.orEmpty() }?.lowercase(Locale.getDefault()).orEmpty()
            title.contains(q) || desc.contains(q) || ingredients.contains(q)
        }
    }
}