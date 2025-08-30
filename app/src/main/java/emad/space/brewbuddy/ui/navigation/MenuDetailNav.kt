package emad.space.brewbuddy.ui.navigation

import android.os.Bundle
import emad.space.domain.models.CoffeeCategory

data class MenuDetailNav(
    val coffeeId: Int,
    val title: String,
    val image: String?,
    val description: String?,
    val category: CoffeeCategory,
    val price: String
) {
    fun toBundle(): Bundle = Bundle().apply {
        putInt("coffeeId", coffeeId)
        putString("title", title)
        putString("image", image)
        putString("description", description)
        putString("category", category.name)
        putString("price", price)
    }
}