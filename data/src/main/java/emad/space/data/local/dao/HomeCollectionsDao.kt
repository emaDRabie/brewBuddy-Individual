package emad.space.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import emad.space.data.local.entities.CatalogEntity
import emad.space.data.local.entities.HomeCollectionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HomeCollectionsDao {

    // Existing: JOIN for ready-to-use catalog rows
    @Query("""
        SELECT c.* FROM catalog AS c
        INNER JOIN home_collections AS hc
            ON c.id = hc.itemId AND c.category = hc.category
        WHERE hc.type = :type
        ORDER BY hc.position ASC
    """)
    fun observeSelection(type: String): Flow<List<CatalogEntity>>

    // NEW: presence check that does NOT depend on catalog being loaded yet
    @Query("SELECT EXISTS(SELECT 1 FROM home_collections WHERE type = :type)")
    fun observeHasType(type: String): Flow<Boolean>

    @Query("DELETE FROM home_collections WHERE type = :type")
    suspend fun clearType(type: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(rows: List<HomeCollectionEntity>)
}