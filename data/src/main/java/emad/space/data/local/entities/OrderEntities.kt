package emad.space.data.local.entities

import androidx.room.*

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey val id: String,
    val placedAtMillis: Long
)

@Entity(
    tableName = "order_items",
    primaryKeys = ["orderId", "coffeeId"],
    foreignKeys = [
        ForeignKey(
            entity = OrderEntity::class,
            parentColumns = ["id"],
            childColumns = ["orderId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("orderId")]
)
data class OrderItemEntity(
    val orderId: String,
    val coffeeId: Int,
    val title: String,
    val image: String?,
    val category: String,
    val quantity: Int,
    val priceCents: Long
)

data class OrderWithItems(
    @Embedded val order: OrderEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "orderId",
        entity = OrderItemEntity::class
    )
    val items: List<OrderItemEntity>
)