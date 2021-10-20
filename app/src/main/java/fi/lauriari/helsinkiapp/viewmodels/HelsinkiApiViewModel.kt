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

class HelsinkiApiViewModel(private val helsinkiApiRepository: HelsinkiApiRepository) : ViewModel() {

    var activitiesResponse: MutableLiveData<Response<HelsinkiActivities>> = MutableLiveData()
    var placesResponse: MutableLiveData<Response<HelsinkiPlaces>> = MutableLiveData()
    var eventsResponse: MutableLiveData<Response<HelsinkiEvents>> = MutableLiveData()

    fun getActivities(tag: String, language: String) {
        viewModelScope.launch {
            val apiResponse = helsinkiApiRepository.getActivities(tag, language)
            activitiesResponse.value = apiResponse
        }
    }

    fun getActivitiesNearby(
        triple: Triple<Double, Double, Double>,
        languageFilter: String,
    ) {
        viewModelScope.launch {
            val apiResponse =
                helsinkiApiRepository.getActivitiesNearby(
                    triple,
                    languageFilter
                )
            activitiesResponse.value = apiResponse
        }
    }

    fun getPlacesNearby(
        triple: Triple<Double, Double, Double>,
        languageFilter: String,
    ) {
        viewModelScope.launch {
            val apiResponse =
                helsinkiApiRepository.getPlacesNearby(
                    triple,
                    languageFilter
                )
            placesResponse.value = apiResponse
        }
    }

    fun getEventsNearby(
        triple: Triple<Double, Double, Double>,
        languageFilter: String,
    ) {
        viewModelScope.launch {
            val apiResponse =
                helsinkiApiRepository.getEventsNearby(
                    triple,
                    languageFilter
                )
            eventsResponse.value = apiResponse
        }
    }



}