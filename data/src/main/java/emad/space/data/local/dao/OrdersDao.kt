package emad.space.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import emad.space.data.local.entities.OrderEntity
import emad.space.data.local.entities.OrderItemEntity
import emad.space.data.local.entities.OrderWithItems
import kotlinx.coroutines.flow.Flow

@Dao
interface OrdersDao {
    @Transaction
    @Query("SELECT * FROM orders ORDER BY placedAtMillis DESC")
    fun observeOrders(): Flow<List<OrderWithItems>>

    @Transaction
    @Query("SELECT * FROM orders WHERE id = :id")
    suspend fun getOrderById(id: String): OrderWithItems?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertOrder(order: OrderEntity)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertItems(items: List<OrderItemEntity>)
}