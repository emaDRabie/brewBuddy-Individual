package emad.space.domain.usecases

import emad.space.domain.handleState.ApiResult
import emad.space.domain.models.CoffeeCategory
import emad.space.domain.models.CoffeeItem
import emad.space.domain.repo.CoffeeRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetCoffeeListUseCase(
    private val repo: CoffeeRepo
) {
    operator fun invoke(category: CoffeeCategory): Flow<ApiResult<List<CoffeeItem>>> {
        return repo.getCoffeeList(category).map { result ->
            when (result) {
                is ApiResult.Success -> ApiResult.Success(result.data.toList())
                is ApiResult.Failure -> result
            }
        }
    }
}