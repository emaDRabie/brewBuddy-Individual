package emad.space.domain.repo

import emad.space.domain.handleState.ApiResult
import emad.space.domain.models.CoffeeCategory
import emad.space.domain.models.CoffeeResponse
import kotlinx.coroutines.flow.Flow

interface CoffeeRepo {
    fun getCoffeeList(category: CoffeeCategory): Flow<ApiResult<CoffeeResponse>>
}