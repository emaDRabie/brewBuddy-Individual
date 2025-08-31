package emad.space.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "catalog")
data class CatalogEntity(
    @PrimaryKey val id: Int,
    val title: String?,
    val description: String?,
    val image: String?,
    val ingredientsJson: String?,
    val category: String // CoffeeCategory.HOT / CoffeeCategory.ICED
)