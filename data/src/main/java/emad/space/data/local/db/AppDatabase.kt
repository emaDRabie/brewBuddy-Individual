package emad.space.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import emad.space.data.local.converters.Converters
import emad.space.data.local.dao.CatalogDao
import emad.space.data.local.dao.FavoritesDao
import emad.space.data.local.dao.HomeCollectionsDao
import emad.space.data.local.dao.OrdersDao
import emad.space.data.local.entities.CatalogEntity
import emad.space.data.local.entities.FavoriteEntity
import emad.space.data.local.entities.HomeCollectionEntity
import emad.space.data.local.entities.OrderEntity
import emad.space.data.local.entities.OrderItemEntity

@Database(
    entities = [
        FavoriteEntity::class,
        OrderEntity::class,
        OrderItemEntity::class,
        CatalogEntity::class,
        HomeCollectionEntity::class
    ],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoritesDao(): FavoritesDao
    abstract fun ordersDao(): OrdersDao
    abstract fun catalogDao(): CatalogDao
    abstract fun homeCollectionsDao(): HomeCollectionsDao

    companion object {
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS home_collections(
                        type TEXT NOT NULL,
                        position INTEGER NOT NULL,
                        itemId INTEGER NOT NULL,
                        category TEXT NOT NULL,
                        PRIMARY KEY(type, position)
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    CREATE UNIQUE INDEX IF NOT EXISTS index_home_collections_type_itemId
                    ON home_collections(type, itemId)
                    """.trimIndent()
                )
            }
        }
    }
}