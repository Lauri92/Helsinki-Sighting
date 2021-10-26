package fi.lauriari.helsinkiapp.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_table")
data class Favorite(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val itemType: String,
    val itemApiId: String
)