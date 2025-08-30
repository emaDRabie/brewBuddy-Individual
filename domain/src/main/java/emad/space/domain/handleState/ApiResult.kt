package emad.space.domain.handleState

sealed interface ApiResult<out T> {
    data class Success<T>(val data: T, val message: String? = null) : ApiResult<T>
    data class Failure(val error: ApiError) : ApiResult<Nothing>
}


sealed class ApiError : Throwable() {
    data class Connection(val reason: String) : ApiError()
    data class Timeout(val reason: String) : ApiError()
    data class Server(val code: Int, override val message: String) : ApiError()
    data class Unknown(override val message: String) : ApiError()
}