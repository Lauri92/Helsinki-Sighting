package fi.lauriari.helsinkiapp.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fi.lauriari.helsinkiapp.datamodels.HelsinkiActivities
import fi.lauriari.helsinkiapp.datamodels.HelsinkiEvents
import fi.lauriari.helsinkiapp.datamodels.HelsinkiPlaces
import fi.lauriari.helsinkiapp.repositories.HelsinkiApiRepository
import kotlinx.coroutines.launch
import retrofit2.Response

class BrowseViewModel : ViewModel() {

    private val helsinkiApiRepository = HelsinkiApiRepository()

    private val activitiesResponse = MutableLiveData<Response<HelsinkiActivities>>()
    private val placesResponse = MutableLiveData<Response<HelsinkiPlaces>>()
    private val eventsResponse = MutableLiveData<Response<HelsinkiEvents>>()

    fun getActivitiesNearby(
        triple: Triple<Double, Double, Double>,
        languageFilter: String,
    ) {
        viewModelScope.launch {
            activitiesResponse.value = helsinkiApiRepository.getActivitiesNearby(
                triple,
                languageFilter
            )
        }
    }

    fun getPlacesNearby(
        triple: Triple<Double, Double, Double>,
        languageFilter: String,
    ) {
        viewModelScope.launch {
            placesResponse.value = helsinkiApiRepository.getPlacesNearby(
                triple,
                languageFilter
            )
        }
    }

    fun getEventsNearby(
        triple: Triple<Double, Double, Double>,
        languageFilter: String,
    ) {
        viewModelScope.launch {
            eventsResponse.value = helsinkiApiRepository.getEventsNearby(
                triple,
                languageFilter
            )
        }
    }

}