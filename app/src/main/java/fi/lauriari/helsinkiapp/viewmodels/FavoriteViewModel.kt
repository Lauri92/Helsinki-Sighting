package fi.lauriari.helsinkiapp.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fi.lauriari.helsinkiapp.database.ItemDB
import fi.lauriari.helsinkiapp.entities.Favorite
import fi.lauriari.helsinkiapp.repositories.FavoriteRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class FavoriteViewModel(application: Application) : AndroidViewModel(application) {

    private val favoriteRepository: FavoriteRepository

    init {
        val favoriteDao = ItemDB.getDatabase(application).favoriteDao()
        favoriteRepository = FavoriteRepository(favoriteDao)
    }

    fun insertFavorite(favorite: Favorite): Long {
        var inventoryId: Long
        runBlocking {
            inventoryId = favoriteRepository.insertFavorite(favorite)
        }
        return inventoryId
    }

    fun getFavoriteList(): List<Favorite> {
        var allFavorites: List<Favorite>
        runBlocking {
            allFavorites = favoriteRepository.getAllFavorites()
        }
        return allFavorites
    }

    fun deleteFavorite(id: Long) {
        runBlocking {
            favoriteRepository.deleteFavorite(id)
        }
    }

}