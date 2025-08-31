package emad.space.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import emad.space.data.local.entities.CatalogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CatalogDao {
    @Query("SELECT * FROM catalog WHERE category = :category ORDER BY title ASC")
    fun observeByCategory(category: String): Flow<List<CatalogEntity>>

    @Query("DELETE FROM catalog WHERE category = :category")
    suspend fun clearCategory(category: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<CatalogEntity>)
}