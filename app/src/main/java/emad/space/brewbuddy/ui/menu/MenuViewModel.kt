package emad.space.brewbuddy.ui.menu

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import emad.space.brewbuddy.util.Event
import emad.space.domain.handleState.ApiResult
import emad.space.domain.models.CoffeeCategory
import emad.space.domain.models.Order
import emad.space.domain.models.OrderItem
import emad.space.domain.models.PricedCoffeeItem
import emad.space.domain.usecases.GetPricedCoffeeListUseCase
import emad.space.domain.usecases.PlaceOrderUseCase
import emad.space.domain.usecases.SearchMenuUseCase
import emad.space.domain.usecases.ToggleFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class MenuUiState(
    val loading: Boolean = false,
    val items: List<PricedCoffeeItem> = emptyList(),
    val allItems: List<PricedCoffeeItem> = emptyList(),
    val error: String? = null,
    val category: CoffeeCategory = CoffeeCategory.HOT
)

data class MenuDetailNav(
    val coffeeId: Int,
    val title: String,
    val image: String?,
    val description: String?,
    val category: CoffeeCategory,
    val price: String
) {
    fun toBundle() = android.os.Bundle().apply {
        putInt("coffeeId", coffeeId)
        putString("title", title)
        putString("image", image)
        putString("description", description)
        putString("category", category.name)
        putString("price", price)
    }
}

@HiltViewModel
class MenuViewModel @Inject constructor(
    private val getPricedList: GetPricedCoffeeListUseCase,
    private val searchUseCase: SearchMenuUseCase,
    private val toggleFavorite: ToggleFavoriteUseCase,
    private val placeOrder: PlaceOrderUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(MenuUiState())
    val state: StateFlow<MenuUiState> = _state

    private val _navigateToDetail = MutableLiveData<Event<MenuDetailNav>>()
    val navigateToDetail: LiveData<Event<MenuDetailNav>> = _navigateToDetail

    fun loadHot() = load(CoffeeCategory.HOT)
    fun loadIced() = load(CoffeeCategory.ICED)

    private fun load(category: CoffeeCategory) {
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true, error = null, category = category)
            getPricedList(category).collectLatest { result ->
                when (result) {
                    is ApiResult.Success -> {
                        _state.value = _state.value.copy(
                            loading = false,
                            items = result.data,
                            allItems = result.data,
                            error = null
                        )
                    }
                    is ApiResult.Failure -> {
                        _state.value = _state.value.copy(
                            loading = false,
                            error = result.error.message ?: "Something went wrong"
                        )
                    }
                }
            }
        }
    }

    fun onSearchQuery(query: String) {
        val base = _state.value.allItems.map { it.item }
        val filteredDomain = searchUseCase(base, query)
        val filtered = filteredDomain.mapNotNull { d ->
            _state.value.allItems.find { it.item.id == d.id }
        }
        _state.value = _state.value.copy(items = filtered)
    }

    fun onCoffeeClicked(item: PricedCoffeeItem) {
        _navigateToDetail.value = Event(
            MenuDetailNav(
                coffeeId = item.item.id ?: -1,
                title = item.item.title.orEmpty(),
                image = item.item.image,
                description = item.item.description, // NEW
                category = _state.value.category,
                price = item.price.toPlainString()
            )
        )
    }

    fun onAddToOrder(item: PricedCoffeeItem) { // NEW
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
}