package emad.space.brewbuddy.ui.orders


import emad.space.domain.usecases.ObserveOrdersUseCase
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import emad.space.domain.models.Order
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OrdersUi(
    val items: List<Order> = emptyList(),
    val allItems: List<Order> = emptyList(),
    val filter: OrdersFilter = OrdersFilter.RECENT
)

enum class OrdersFilter { RECENT, PAST }

@HiltViewModel
class OrdersViewModel @Inject constructor(
    observeOrders: ObserveOrdersUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(OrdersUi())
    val state: StateFlow<OrdersUi> = _state

    // Define "recent" as the last 1 hour
    private val recentWindowMillis: Long = 1L * 60L * 60L * 1000L

    init {
        viewModelScope.launch {
            observeOrders().collectLatest { list ->
                val currentFilter = _state.value.filter
                _state.value = _state.value.copy(
                    allItems = list,
                    items = applyFilter(list, currentFilter)
                )
            }
        }
    }

    fun setRecent() = setFilter(OrdersFilter.RECENT)
    fun setPast() = setFilter(OrdersFilter.PAST)

    private fun setFilter(filter: OrdersFilter) {
        val all = _state.value.allItems
        _state.value = _state.value.copy(
            filter = filter,
            items = applyFilter(all, filter)
        )
    }

    private fun applyFilter(list: List<Order>, filter: OrdersFilter): List<Order> {
        val now = System.currentTimeMillis()
        val cutoff = now - recentWindowMillis
        return when (filter) {
            OrdersFilter.RECENT -> list.filter { it.placedAtEpochMillis >= cutoff }
            OrdersFilter.PAST -> list.filter { it.placedAtEpochMillis < cutoff }
        }
        // DAO already sorts by date desc; filtering preserves order.
    }
}