package emad.space.data.remote

import emad.space.domain.models.CoffeeResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface CoffeeApiService {
    // Base URL: https://api.sampleapis.com/
    @GET("coffee/{category}")
    suspend fun getCoffee(@Path("category") categoryPath: String): CoffeeResponse
}