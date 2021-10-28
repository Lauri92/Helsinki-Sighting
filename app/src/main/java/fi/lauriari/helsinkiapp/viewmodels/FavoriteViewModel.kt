package fi.lauriari.helsinkiapp.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import fi.lauriari.helsinkiapp.classes.SingleHelsinkiItem
import fi.lauriari.helsinkiapp.database.ItemDB
import fi.lauriari.helsinkiapp.datamodels.SingleHelsinkiActivity
import fi.lauriari.helsinkiapp.datamodels.SingleHelsinkiEvent
import fi.lauriari.helsinkiapp.datamodels.SingleHelsinkiPlace
import fi.lauriari.helsinkiapp.entities.Favorite
import fi.lauriari.helsinkiapp.repositories.FavoriteRepository
import fi.lauriari.helsinkiapp.repositories.HelsinkiApiRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Response

class FavoriteViewModel(application: Application) : AndroidViewModel(application) {

    private val favoriteRepository: FavoriteRepository
    private val helsinkiApiRepository = HelsinkiApiRepository()

    val favoriteActivities = MutableLiveData<MutableList<SingleHelsinkiItem>>()
    val favoritePlaces = MutableLiveData<MutableList<SingleHelsinkiItem>>()
    val favoriteEvents = MutableLiveData<MutableList<SingleHelsinkiItem>>()

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

    fun getFavoriteActivities() {
        val listForAdapter = mutableListOf<SingleHelsinkiItem>()
        viewModelScope.launch {
            var favoriteList: List<Favorite>? = emptyList()
            val favorites = async {
                favoriteList = getFavoriteList().toMutableList().filter {
                    it.itemType == "MyHelsinki"
                }
            }
            favorites.await()
            if (favoriteList?.isNotEmpty() == true) {
                favoriteList?.forEach {
                    var itemResponse: Response<SingleHelsinkiActivity>? = null
                    val getSingleItem = async {
                        itemResponse = helsinkiApiRepository.getActivityById(it.itemApiId, "en")
                    }
                    getSingleItem.await()
                    if (itemResponse?.isSuccessful == true) {
                        val addSingleItem = async {
                            listForAdapter.add(
                                SingleHelsinkiItem(
                                    id = itemResponse?.body()?.id,
                                    name = itemResponse?.body()?.name?.en,
                                    infoUrl = itemResponse?.body()?.info_url,
                                    latitude = itemResponse?.body()?.location?.lat,
                                    longitude = itemResponse?.body()?.location?.lon,
                                    streetAddress = itemResponse?.body()?.location?.address?.street_address,
                                    locality = itemResponse?.body()?.location?.address?.locality,
                                    description = itemResponse?.body()?.description?.body,
                                    images = itemResponse?.body()?.description?.images,
                                    tags = itemResponse?.body()?.tags,
                                    whereWhenDuration = itemResponse?.body()?.where_when_duration,
                                    itemType = itemResponse?.body()?.source_type!!.name
                                )
                            )
                        }
                        addSingleItem.await()
                        favoriteActivities.value = listForAdapter
                    }
                }
            } else {
                // list is empty
                favoriteActivities.value = listForAdapter
            }
        }
    }


    fun getFavoritePlaces() {
        val listForAdapter = mutableListOf<SingleHelsinkiItem>()
        viewModelScope.launch {
            var favoriteList: List<Favorite>? = emptyList()
            val favorites = async {
                favoriteList = getFavoriteList().toMutableList().filter {
                    it.itemType == "Matko"
                }
            }
            favorites.await()
            if (favoriteList?.isNotEmpty() == true) {
                favoriteList?.forEach {
                    var itemResponse: Response<SingleHelsinkiPlace>? = null
                    val getSingleItem = async {
                        itemResponse = helsinkiApiRepository.getPlaceById(it.itemApiId, "en")
                    }
                    getSingleItem.await()
                    if (itemResponse?.isSuccessful == true) {
                        val addSingleItem = async {
                            listForAdapter.add(
                                SingleHelsinkiItem(
                                    id = itemResponse?.body()?.id,
                                    name = itemResponse?.body()?.name?.en,
                                    infoUrl = itemResponse?.body()?.info_url,
                                    latitude = itemResponse?.body()?.location?.lat,
                                    longitude = itemResponse?.body()?.location?.lon,
                                    streetAddress = itemResponse?.body()?.location?.address?.street_address,
                                    locality = itemResponse?.body()?.location?.address?.locality,
                                    description = itemResponse?.body()?.description?.body,
                                    images = itemResponse?.body()?.description?.images,
                                    tags = itemResponse?.body()?.tags,
                                    openingHours = itemResponse?.body()?.opening_hours,
                                    itemType = itemResponse?.body()?.source_type!!.name
                                )
                            )
                        }
                        addSingleItem.await()
                        favoritePlaces.value = listForAdapter
                    }
                }
            } else {
                // list is empty
                favoritePlaces.value = listForAdapter
            }
        }
    }


    fun getFavoriteEvents() {
        val listForAdapter = mutableListOf<SingleHelsinkiItem>()
        viewModelScope.launch {
            var favoriteList: List<Favorite>? = emptyList()
            val favorites = async {
                favoriteList = getFavoriteList().toMutableList().filter {
                    it.itemType == "LinkedEvents"
                }
            }
            favorites.await()
            if (favoriteList?.isNotEmpty() == true) {
                favoriteList?.forEach {
                    var itemResponse: Response<SingleHelsinkiEvent>? = null
                    val getSingleItem = async {
                        itemResponse = helsinkiApiRepository.getEventById(it.itemApiId, "en")
                    }
                    getSingleItem.await()
                    if (itemResponse?.isSuccessful == true) {
                        val addSingleItem = async {
                            listForAdapter.add(
                                SingleHelsinkiItem(
                                    id = itemResponse?.body()?.id,
                                    name = itemResponse?.body()?.name?.en,
                                    infoUrl = itemResponse?.body()?.info_url,
                                    latitude = itemResponse?.body()?.location?.lat,
                                    longitude = itemResponse?.body()?.location?.lon,
                                    streetAddress = itemResponse?.body()?.location?.address?.street_address,
                                    locality = itemResponse?.body()?.location?.address?.locality,
                                    description = itemResponse?.body()?.description?.body,
                                    images = itemResponse?.body()?.description?.images,
                                    tags = itemResponse?.body()?.tags,
                                    eventDates = itemResponse?.body()?.event_dates,
                                    itemType = itemResponse?.body()?.source_type!!.name
                                )
                            )
                        }
                        addSingleItem.await()
                        favoriteEvents.value = listForAdapter
                    }
                }
            } else {
                // list is empty
                favoriteEvents.value = listForAdapter
            }
        }
    }
}