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

data class OrdersUi(val items: List<Order> = emptyList())

@HiltViewModel
class OrdersViewModel @Inject constructor(
    observeOrders: ObserveOrdersUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(OrdersUi())
    val state: StateFlow<OrdersUi> = _state

    init {
        viewModelScope.launch {
            observeOrders().collectLatest { _state.value = OrdersUi(it) }
        }
    }
}