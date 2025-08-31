package emad.space.brewbuddy.ui.payment

import android.os.Bundle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import emad.space.domain.models.CoffeeCategory
import emad.space.domain.models.CoffeeItem
import emad.space.domain.models.Order
import emad.space.domain.models.OrderItem
import emad.space.domain.models.PricedCoffeeItem
import emad.space.domain.usecases.PlaceOrderUseCase
import java.math.BigDecimal
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val placeOrderUseCase: PlaceOrderUseCase
) : ViewModel() {

    suspend fun placeOrderFromArgs(args: Bundle): String {
        val coffeeId = args.getInt("coffeeId", 0)
        val title = args.getString("orderTitle").orEmpty()
        val description = args.getString("orderDescription")
        val image = args.getString("orderImage")
        val category = CoffeeCategory.valueOf(
            args.getString("orderCategory") ?: CoffeeCategory.HOT.name
        )
        val qty = args.getInt("orderQty", 1)
        val unitPrice = args.getString("orderPrice")?.toBigDecimalOrNull() ?: BigDecimal.ZERO

        val item = PricedCoffeeItem(
            item = CoffeeItem(
                title = title,
                description = description,
                ingredients = null,
                image = image,
                id = coffeeId
            ),
            category = category,
            price = unitPrice
        )
        val order = Order(
            id = UUID.randomUUID().toString(),
            items = listOf(OrderItem(coffee = item, quantity = qty)),
            placedAtEpochMillis = System.currentTimeMillis()
        )
        return placeOrderUseCase(order)
    }
}