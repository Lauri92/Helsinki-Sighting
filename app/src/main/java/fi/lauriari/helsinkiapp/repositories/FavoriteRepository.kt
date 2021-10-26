package fi.lauriari.helsinkiapp.repositories

import fi.lauriari.helsinkiapp.daos.FavoriteDao
import fi.lauriari.helsinkiapp.entities.Favorite

class FavoriteRepository(private val favoriteDao: FavoriteDao) {

    suspend fun insertFavorite(favorite: Favorite): Long {
        return favoriteDao.insertFavorite(favorite)
    }

    suspend fun getAllFavorites(): List<Favorite> {
        return favoriteDao.getAllFavorites()
    }

    suspend fun deleteFavorite(id: Long) {
        favoriteDao.deleteFavorite(id)
    }

}