package emad.space.domain.repo

import emad.space.domain.models.CoffeeCategory
import emad.space.domain.models.CoffeeItem
import kotlinx.coroutines.flow.Flow

interface HomeCollectionsRepo {
    fun observeBestSeller(): Flow<CoffeeItem?>
    fun observeRecommendations(): Flow<List<CoffeeItem>>

    // NEW: know if there is already a persisted selection (avoid reseeding)
    fun observeHasBestSeller(): Flow<Boolean>
    fun observeHasRecommendations(): Flow<Boolean>

    suspend fun setBestSeller(itemId: Int, category: CoffeeCategory)
    suspend fun setRecommendations(items: List<Pair<Int, CoffeeCategory>>)
}