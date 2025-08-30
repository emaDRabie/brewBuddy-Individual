package emad.space.domain.usecases

import emad.space.domain.models.CoffeeItem
import emad.space.domain.repo.FavoritesRepo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveFavoritesUseCase @Inject constructor(
    private val repo: FavoritesRepo
) {
    operator fun invoke(): Flow<List<CoffeeItem>> = repo.observeFavorites()
}

class ToggleFavoriteUseCase @Inject constructor(
    private val repo: FavoritesRepo
) {
    suspend operator fun invoke(item: CoffeeItem): Boolean = repo.toggleFavorite(item)
}