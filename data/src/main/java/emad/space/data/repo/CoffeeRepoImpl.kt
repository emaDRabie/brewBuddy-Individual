package emad.space.data.repo

import emad.space.data.local.converters.Converters
import emad.space.data.local.dao.CatalogDao
import emad.space.data.local.entities.CatalogEntity
import emad.space.data.remote.CoffeeApiService
import emad.space.domain.handleState.ApiError
import emad.space.domain.handleState.ApiResult
import emad.space.domain.models.CoffeeCategory
import emad.space.domain.models.CoffeeItem
import emad.space.domain.models.CoffeeResponse
import emad.space.domain.repo.CoffeeRepo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class CoffeeRepoImpl(
    private val api: CoffeeApiService,
    private val catalogDao: CatalogDao,
    private val io: CoroutineDispatcher = Dispatchers.IO
) : CoffeeRepo {

    override fun getCoffeeList(category: CoffeeCategory): Flow<ApiResult<CoffeeResponse>> = flow {
        // 1) Emit cached data immediately (even if empty) to keep UI responsive/offline
        val cached = catalogDao.observeByCategory(category.name).first()
        val cachedItems = cached.map { it.toDomain() }
        emit(ApiResult.Success(cachedItems.toCoffeeResponse()))

        // 2) Try to refresh from network
        try {
            val remote = api.getCoffee(category.path)

            // Persist fresh data to cache
            val entities = remote.map { it.toEntity(category.name) }
            catalogDao.clearCategory(category.name)
            catalogDao.upsertAll(entities)

            // Emit fresh list
            emit(ApiResult.Success(remote))
        } catch (e: UnknownHostException) {
            emit(ApiResult.Failure(ApiError.Connection("No internet connection")))
        } catch (e: SocketTimeoutException) {
            emit(ApiResult.Failure(ApiError.Timeout("Request timed out")))
        } catch (e: HttpException) {
            emit(ApiResult.Failure(ApiError.Server(e.code(), e.message())))
        } catch (e: Throwable) {
            emit(ApiResult.Failure(ApiError.Unknown(e.message ?: "Unknown error")))
        }
    }.flowOn(io)

    private fun CatalogEntity.toDomain() = CoffeeItem(
        title = title,
        description = description,
        ingredients = Converters.jsonToList(ingredientsJson),
        image = image,
        id = id
    )

    private fun CoffeeItem.toEntity(category: String) = CatalogEntity(
        id = id ?: 0,
        title = title,
        description = description,
        image = image,
        ingredientsJson = Converters.listToJson(ingredients),
        category = category
    )

    private fun List<CoffeeItem>.toCoffeeResponse(): CoffeeResponse =
        CoffeeResponse().apply { addAll(this@toCoffeeResponse) }
}