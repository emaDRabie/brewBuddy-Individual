package emad.space.data.repo

import emad.space.data.remote.CoffeeApiService
import emad.space.domain.handleState.ApiError
import emad.space.domain.handleState.ApiResult
import emad.space.domain.models.CoffeeCategory
import emad.space.domain.models.CoffeeResponse
import emad.space.domain.repo.CoffeeRepo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class CoffeeRepoImpl(
    private val api: CoffeeApiService,
    private val io: CoroutineDispatcher = Dispatchers.IO
) : CoffeeRepo {

    override fun getCoffeeList(category: CoffeeCategory): Flow<ApiResult<CoffeeResponse>> = flow {
        try {
            val body = api.getCoffee(category.path)
            emit(ApiResult.Success(body))
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
}