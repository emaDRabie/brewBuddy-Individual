package emad.space.data.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import emad.space.data.local.db.AppDatabase
import emad.space.data.local.dao.FavoritesDao
import emad.space.data.local.dao.OrdersDao
import emad.space.data.pricing.PricingRepoImpl
import emad.space.data.remote.CoffeeApiService
import emad.space.data.repo.CoffeeRepoImpl
import emad.space.data.repo.FavoritesRepoImpl
import emad.space.data.repo.OrdersRepoImpl
import emad.space.data.prefs.UserPrefsRepoImpl
import emad.space.domain.repo.CoffeeRepo
import emad.space.domain.repo.FavoritesRepo
import emad.space.domain.repo.OrdersRepo
import emad.space.domain.repo.PricingRepo
import emad.space.domain.repo.UserPrefsRepo
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides @Singleton
    fun provideOkHttp(): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
        .build()

    @Provides @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://api.sampleapis.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides @Singleton
    fun provideCoffeeApi(retrofit: Retrofit): CoffeeApiService =
        retrofit.create(CoffeeApiService::class.java)

    // Room
    @Provides @Singleton
    fun provideDb(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "brewbuddy.db").build()

    @Provides fun provideFavoritesDao(db: AppDatabase): FavoritesDao = db.favoritesDao()
    @Provides fun provideOrdersDao(db: AppDatabase): OrdersDao = db.ordersDao()

    // Repos (domain interfaces -> data implementations)
    @Provides @Singleton
    fun provideCoffeeRepo(api: CoffeeApiService): CoffeeRepo = CoffeeRepoImpl(api)

    @Provides @Singleton
    fun provideFavoritesRepo(dao: FavoritesDao): FavoritesRepo = FavoritesRepoImpl(dao)

    @Provides @Singleton
    fun provideOrdersRepo(dao: OrdersDao): OrdersRepo = OrdersRepoImpl(dao)

    @Provides @Singleton
    fun provideUserPrefsRepo(@ApplicationContext context: Context): UserPrefsRepo =
        UserPrefsRepoImpl(context)

    @Provides @Singleton
    fun providePricingRepo(): PricingRepo = PricingRepoImpl()
}