package fi.lauriari.helsinkiapp.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fi.lauriari.helsinkiapp.datamodels.HelsinkiActivities
import fi.lauriari.helsinkiapp.datamodels.HelsinkiEvents
import fi.lauriari.helsinkiapp.datamodels.HelsinkiPlaces
import fi.lauriari.helsinkiapp.datamodels.SingleHelsinkiActivity
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

}