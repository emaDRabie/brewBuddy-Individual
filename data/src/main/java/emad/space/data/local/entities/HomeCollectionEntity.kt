package emad.space.data.local.entities

import androidx.room.Entity
import androidx.room.Index


@Entity(
    tableName = "home_collections",
    primaryKeys = ["type", "position"],
    indices = [
        Index(value = ["type", "itemId"], unique = true)
    ]
)
data class HomeCollectionEntity(
    val type: String,
    val position: Int,
    val itemId: Int,
    val category: String
)