package emad.space.domain.models

data class CoffeeItem(
    val title: String? = "",
    val description: String? = "",
    val ingredients: List<String?>? = emptyList(),
    val image: String? = "",
    val id: Int? = 0,
)