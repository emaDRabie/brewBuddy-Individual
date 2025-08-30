package emad.space.domain.usecases

import emad.space.domain.handleState.ApiResult
import emad.space.domain.models.CoffeeCategory
import emad.space.domain.models.PricedCoffeeItem
import emad.space.domain.repo.CoffeeRepo
import emad.space.domain.repo.PricingRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetPricedCoffeeListUseCase @Inject constructor(
    private val coffeeRepo: CoffeeRepo,
    private val pricingRepo: PricingRepo
) {
    operator fun invoke(category: CoffeeCategory): Flow<ApiResult<List<PricedCoffeeItem>>> {
        return coffeeRepo.getCoffeeList(category).map { result ->
            when (result) {
                is ApiResult.Success -> {
                    val priced = result.data.map { item ->
                        PricedCoffeeItem(
                            item = item,
                            category = category,
                            price = pricingRepo.getPriceFor(item, category)
                        )
                    }
                    ApiResult.Success(priced)
                }

                is ApiResult.Failure -> result
            }
        }
    }
}