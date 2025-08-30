package emad.space.domain.repo

import emad.space.domain.models.CoffeeItem
import kotlinx.coroutines.flow.Flow

interface FavoritesRepo {
    fun observeFavorites(): Flow<List<CoffeeItem>>
    fun isFavorite(id: Int): Flow<Boolean>
    suspend fun setFavorite(item: CoffeeItem, favorite: Boolean)
    suspend fun toggleFavorite(item: CoffeeItem): Boolean
}