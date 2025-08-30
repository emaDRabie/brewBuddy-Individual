package emad.space.brewbuddy.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import emad.space.domain.models.CoffeeCategory
import emad.space.domain.models.CoffeeItem
import emad.space.domain.models.Order
import emad.space.domain.models.OrderItem
import emad.space.domain.models.PricedCoffeeItem
import emad.space.domain.repo.FavoritesRepo
import emad.space.domain.usecases.PlaceOrderUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CoffeeDetailViewModel @Inject constructor(
    private val placeOrderUseCase: PlaceOrderUseCase,
    private val favoritesRepo: FavoritesRepo
) : ViewModel() {

    var coffeeId: Int = -1
        private set
    var title: String = ""
        private set
    var image: String? = null
        private set
    var description: String? = null
        private set
    var category: CoffeeCategory = CoffeeCategory.HOT
        private set
    var price: BigDecimal = BigDecimal.ZERO
        private set
    var quantity: Int = 1
        private set

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()

    fun bindArgs(args: android.os.Bundle) {
        coffeeId = args.getInt("coffeeId")
        title = args.getString("title").orEmpty()
        image = args.getString("image")
        description = args.getString("description")
        category = CoffeeCategory.valueOf(args.getString("category") ?: CoffeeCategory.HOT.name)
        price = args.getString("price")?.toBigDecimalOrNull() ?: BigDecimal.ZERO

        if (coffeeId >= 0) {
            viewModelScope.launch {
                favoritesRepo.isFavorite(coffeeId).collectLatest { fav -> _isFavorite.value = fav }
            }
        }
    }

    fun increment() { quantity++ }
    fun decrement() { if (quantity > 1) quantity-- }

    fun total(): BigDecimal = price.multiply(BigDecimal(quantity))

    fun placeOrder() {
        viewModelScope.launch {
            val item = PricedCoffeeItem(
                item = CoffeeItem(
                    title = title,
                    description = description,
                    ingredients = null,
                    image = image,
                    id = coffeeId
                ),
                category = category,
                price = price
            )
            val order = Order(
                id = UUID.randomUUID().toString(),
                items = listOf(OrderItem(coffee = item, quantity = quantity)),
                placedAtEpochMillis = System.currentTimeMillis()
            )
            placeOrderUseCase(order)
        }
    }

    fun toggleFavorite() {
        if (coffeeId < 0) return
        viewModelScope.launch {
            val target = !_isFavorite.value
            val item = CoffeeItem(
                title = title,
                description = description,
                ingredients = null,
                image = image,
                id = coffeeId
            )
            favoritesRepo.setFavorite(item, target)
        }
    }
}