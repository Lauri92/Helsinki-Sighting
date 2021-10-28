package fi.lauriari.helsinkiapp.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fi.lauriari.helsinkiapp.datamodels.HelsinkiActivities
import fi.lauriari.helsinkiapp.datamodels.HelsinkiEvents
import fi.lauriari.helsinkiapp.datamodels.HelsinkiPlaces
import fi.lauriari.helsinkiapp.repositories.HelsinkiApiRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.osmdroid.util.GeoPoint
import retrofit2.Response

class SearchViewModel : ViewModel() {

    private val helsinkiApiRepository = HelsinkiApiRepository()

    var activitiesResponse = MutableLiveData<Response<HelsinkiActivities>>()
    var placesResponse = MutableLiveData<Response<HelsinkiPlaces>>()
    var eventsResponse = MutableLiveData<Response<HelsinkiEvents>>()


    fun getActivities(tag: String, language: String) {
        viewModelScope.launch {
            activitiesResponse.value = helsinkiApiRepository.getActivities(tag, language)
        }
    }

    fun getPlaces(tag: String, language: String) {
        viewModelScope.launch {
            placesResponse.value = helsinkiApiRepository.getPlaces(tag, language)
        }
    }

    fun getEvents(tag: String, language: String) {
        viewModelScope.launch {
            eventsResponse.value = helsinkiApiRepository.getEvents(tag, language)
        }
    }

}