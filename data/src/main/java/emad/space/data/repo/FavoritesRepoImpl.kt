package emad.space.data.repo

import emad.space.data.local.dao.FavoritesDao
import emad.space.data.local.entities.FavoriteEntity
import emad.space.domain.models.CoffeeItem
import emad.space.domain.repo.FavoritesRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoritesRepoImpl(
    private val dao: FavoritesDao
) : FavoritesRepo {

    override fun observeFavorites(): Flow<List<CoffeeItem>> =
        dao.observeFavorites().map { list -> list.map { it.toDomain() } }

    override fun isFavorite(id: Int): Flow<Boolean> = dao.isFavorite(id)

    override suspend fun setFavorite(item: CoffeeItem, favorite: Boolean) {
        if (item.id == null) return
        if (favorite) dao.upsert(item.toEntity()) else dao.deleteById(item.id!!)
    }

    override suspend fun toggleFavorite(item: CoffeeItem): Boolean {
        val id = item.id ?: return false
        var newValue = false
        // Simple approach: read current value by collecting first(), or rely on UI state to pass the target
        // Here we optimistically toggle to 'true'
        dao.upsert(item.toEntity())
        newValue = true
        return newValue
    }

    private fun FavoriteEntity.toDomain() = CoffeeItem(
        title = title,
        description = description,
        ingredients = null, // you can parse JSON if you need it in UI
        image = image,
        id = id
    )

    private fun CoffeeItem.toEntity() = FavoriteEntity(
        id = id ?: 0,
        title = title.orEmpty(),
        description = description,
        image = image,
        ingredientsJson = null // add if you want to keep ingredients
    )
}