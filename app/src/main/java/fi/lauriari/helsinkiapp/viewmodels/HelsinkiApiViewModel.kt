package fi.lauriari.helsinkiapp.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fi.lauriari.helsinkiapp.datamodels.HelsinkiActivities
import fi.lauriari.helsinkiapp.datamodels.HelsinkiEvents
import fi.lauriari.helsinkiapp.datamodels.HelsinkiPlaces
import fi.lauriari.helsinkiapp.repositories.HelsinkiApiRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Response

class HelsinkiApiViewModel(private val helsinkiApiRepository: HelsinkiApiRepository) : ViewModel() {


    fun getActivities(tag: String, language: String) {
        viewModelScope.launch {
            val apiResponse = helsinkiApiRepository.getActivities(tag, language)
        }
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
}