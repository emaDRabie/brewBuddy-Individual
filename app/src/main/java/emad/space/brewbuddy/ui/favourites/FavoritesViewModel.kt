package emad.space.brewbuddy.ui.favourites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import emad.space.brewbuddy.ui.navigation.MenuDetailNav
import emad.space.domain.models.CoffeeCategory
import emad.space.domain.models.Order
import emad.space.domain.models.OrderItem
import emad.space.domain.models.PricedCoffeeItem
import emad.space.domain.repo.PricingRepo
import emad.space.domain.usecases.ObserveFavoritesUseCase
import emad.space.domain.usecases.PlaceOrderUseCase
import emad.space.domain.usecases.ToggleFavoriteUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class FavoritesState(val items: List<PricedCoffeeItem> = emptyList())

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    observeFavorites: ObserveFavoritesUseCase,
    private val pricingRepo: PricingRepo,
    private val toggleFavorite: ToggleFavoriteUseCase,
    private val placeOrder: PlaceOrderUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(FavoritesState())
    val state: StateFlow<FavoritesState> = _state

    private val _navigateToDetail = MutableSharedFlow<MenuDetailNav>(replay = 0, extraBufferCapacity = 1)
    val navigateToDetail: SharedFlow<MenuDetailNav> = _navigateToDetail.asSharedFlow()

    init {
        viewModelScope.launch {
            observeFavorites().collectLatest { list ->
                val priced = list.map {
                    PricedCoffeeItem(
                        it,
                        CoffeeCategory.HOT,
                        pricingRepo.getPriceFor(it, CoffeeCategory.HOT)
                    )
                }
                _state.value = FavoritesState(priced)
            }
        }
    }

    fun onAddToOrder(item: PricedCoffeeItem) {
        viewModelScope.launch {
            val order = Order(
                id = UUID.randomUUID().toString(),
                items = listOf(OrderItem(coffee = item, quantity = 1)),
                placedAtEpochMillis = System.currentTimeMillis()
            )
            placeOrder(order)
        }
    }

    fun onToggleFavorite(item: PricedCoffeeItem) {
        viewModelScope.launch { toggleFavorite(item.item) }
    }

    fun onCoffeeClicked(item: PricedCoffeeItem) {
        _navigateToDetail.tryEmit(
            MenuDetailNav(
                coffeeId = item.item.id ?: -1,
                title = item.item.title.orEmpty(),
                image = item.item.image,
                description = item.item.description,
                category = item.category,
                price = item.price.toPlainString()
            )
        )
    }
}