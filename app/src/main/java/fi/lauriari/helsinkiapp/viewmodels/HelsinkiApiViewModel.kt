package fi.lauriari.helsinkiapp.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fi.lauriari.helsinkiapp.datamodels.*
import fi.lauriari.helsinkiapp.repositories.HelsinkiApiRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Response

class HelsinkiApiViewModel : ViewModel() {

    private val helsinkiApiRepository = HelsinkiApiRepository()

    fun getActivities(tag: String, language: String): Response<HelsinkiActivities> {
        var apiResponse: Response<HelsinkiActivities>
        runBlocking {
            apiResponse = helsinkiApiRepository.getActivities(tag, language)
        }
        return apiResponse
    }

    fun getPlaces(tag: String, language: String): Response<HelsinkiPlaces> {
        var apiResponse: Response<HelsinkiPlaces>
        runBlocking {
            apiResponse = helsinkiApiRepository.getPlaces(tag, language)
        }
        return apiResponse
    }

    fun getEvents(tag: String, language: String): Response<HelsinkiEvents> {
        var apiResponse: Response<HelsinkiEvents>
        runBlocking {
            apiResponse = helsinkiApiRepository.getEvents(tag, language)
        }
        return apiResponse
    }

    fun getActivitiesNearby(
        triple: Triple<Double, Double, Double>,
        languageFilter: String,
    ): Response<HelsinkiActivities> {
        var apiResponse: Response<HelsinkiActivities>
        runBlocking {
            apiResponse = helsinkiApiRepository.getActivitiesNearby(
                triple,
                languageFilter
            )
        }
        return apiResponse
    }

    fun getPlacesNearby(
        triple: Triple<Double, Double, Double>,
        languageFilter: String,
    ): Response<HelsinkiPlaces> {
        var apiResponse: Response<HelsinkiPlaces>
        runBlocking {
            apiResponse = helsinkiApiRepository.getPlacesNearby(
                triple,
                languageFilter
            )
        }
        return apiResponse
    }

    fun getEventsNearby(
        triple: Triple<Double, Double, Double>,
        languageFilter: String,
    ): Response<HelsinkiEvents> {
        var apiResponse: Response<HelsinkiEvents>
        runBlocking {
            apiResponse = helsinkiApiRepository.getEventsNearby(
                triple,
                languageFilter
            )
        }
        return apiResponse
    }

    fun getActivityById(id: String, languageFilter: String): Response<SingleHelsinkiActivity> {
        var apiResponse: Response<SingleHelsinkiActivity>
        runBlocking {
            apiResponse = helsinkiApiRepository.getActivityById(id, languageFilter)
        }
        return apiResponse
    }

    fun getPlaceById(id: String, languageFilter: String): Response<SingleHelsinkiPlace> {
        var apiResponse: Response<SingleHelsinkiPlace>
        runBlocking {
            apiResponse = helsinkiApiRepository.getPlaceById(id, languageFilter)
        }
        return apiResponse
    }

    fun getEventById(id: String, languageFilter: String): Response<SingleHelsinkiEvent> {
        var apiResponse: Response<SingleHelsinkiEvent>
        runBlocking {
            apiResponse = helsinkiApiRepository.getEventById(id, languageFilter)
        }
        return apiResponse
    }

}