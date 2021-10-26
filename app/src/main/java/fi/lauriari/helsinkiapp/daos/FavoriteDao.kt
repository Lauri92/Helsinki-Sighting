package fi.lauriari.helsinkiapp.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import fi.lauriari.helsinkiapp.entities.Favorite


@Dao
interface FavoriteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: Favorite): Long

    @Query("SELECT * FROM favorite_table")
    suspend fun getAllFavorites(): List<Favorite>

    @Query("DELETE FROM favorite_table WHERE id = :id")
    suspend fun deleteFavorite(id: Long)

}