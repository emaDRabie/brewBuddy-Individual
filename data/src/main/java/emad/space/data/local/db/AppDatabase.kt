package emad.space.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import emad.space.data.local.converters.Converters
import emad.space.data.local.dao.FavoritesDao
import emad.space.data.local.dao.OrdersDao
import emad.space.data.local.entities.FavoriteEntity
import emad.space.data.local.entities.OrderEntity
import emad.space.data.local.entities.OrderItemEntity

@Database(
    entities = [FavoriteEntity::class, OrderEntity::class, OrderItemEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoritesDao(): FavoritesDao
    abstract fun ordersDao(): OrdersDao
}