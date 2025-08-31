package emad.space.data.repo

import emad.space.data.local.converters.Converters
import emad.space.data.local.dao.HomeCollectionsDao
import emad.space.data.local.entities.CatalogEntity
import emad.space.data.local.entities.HomeCollectionEntity
import emad.space.domain.models.CoffeeCategory
import emad.space.domain.models.CoffeeItem
import emad.space.domain.repo.HomeCollectionsRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val TYPE_BEST = "BEST"
private const val TYPE_RECOMMENDATION = "RECOMMENDATION"

class HomeCollectionsRepoImpl(
    private val dao: HomeCollectionsDao
) : HomeCollectionsRepo {

    override fun observeBestSeller(): Flow<CoffeeItem?> =
        dao.observeSelection(TYPE_BEST).map { list -> list.firstOrNull()?.toDomain() }

    override fun observeRecommendations(): Flow<List<CoffeeItem>> =
        dao.observeSelection(TYPE_RECOMMENDATION).map { list -> list.map { it.toDomain() } }

    override fun observeHasBestSeller(): Flow<Boolean> =
        dao.observeHasType(TYPE_BEST)

    override fun observeHasRecommendations(): Flow<Boolean> =
        dao.observeHasType(TYPE_RECOMMENDATION)

    override suspend fun setBestSeller(itemId: Int, category: CoffeeCategory) {
        dao.clearType(TYPE_BEST)
        dao.upsertAll(
            listOf(HomeCollectionEntity(TYPE_BEST, 0, itemId, category.name))
        )
    }

    override suspend fun setRecommendations(items: List<Pair<Int, CoffeeCategory>>) {
        dao.clearType(TYPE_RECOMMENDATION)
        dao.upsertAll(
            items.mapIndexed { index, (id, category) ->
                HomeCollectionEntity(TYPE_RECOMMENDATION, index, id, category.name)
            }
        )
    }

    private fun CatalogEntity.toDomain() = CoffeeItem(
        title = title,
        description = description,
        ingredients = Converters.jsonToList(ingredientsJson),
        image = image,
        id = id
    )
}