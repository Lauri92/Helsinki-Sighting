package fi.lauriari.helsinkiapp.repositories

import android.util.Log
import fi.lauriari.helsinkiapp.datamodels.*
import fi.lauriari.helsinkiapp.network.HelsinkiApiRetrofitInstance
import retrofit2.Response

class HelsinkiApiRepository {

    suspend fun getActivities(tag: String, language: String): Response<HelsinkiActivities> {
        return HelsinkiApiRetrofitInstance.api.getActivities(tag, language)
    }

    suspend fun getPlaces(tag: String, language: String): Response<HelsinkiPlaces> {
        return HelsinkiApiRetrofitInstance.api.getPlaces(tag, language)
    }

    suspend fun getEvents(tag: String, language: String): Response<HelsinkiEvents> {
        return HelsinkiApiRetrofitInstance.api.getEvents(tag, language)
    }

    suspend fun getActivitiesNearby(
        triple: Triple<Double, Double, Double>,
        languageFilter: String
    ): Response<HelsinkiActivities> {
        val stringRepresentation = "${triple.first},${triple.second},${triple.third}"
        return HelsinkiApiRetrofitInstance.api.getActivitiesNearby(
            stringRepresentation,
            languageFilter
        )
    }

    suspend fun getPlacesNearby(
        triple: Triple<Double, Double, Double>,
        languageFilter: String,
    ): Response<HelsinkiPlaces> {
        val stringRepresentation = "${triple.first},${triple.second},${triple.third}"
        return HelsinkiApiRetrofitInstance.api.getPlacesNearby(
            stringRepresentation,
            languageFilter
        )
    }

    suspend fun getEventsNearby(
        triple: Triple<Double, Double, Double>,
        languageFilter: String
    ): Response<HelsinkiEvents> {
        val stringRepresentation = "${triple.first},${triple.second},${triple.third}"
        return HelsinkiApiRetrofitInstance.api.getEventsNearby(
            stringRepresentation,
            languageFilter
        )
    }

    suspend fun getActivityById(
        id: String,
        languageFilter: String
    ): Response<SingleHelsinkiActivity> {
        return HelsinkiApiRetrofitInstance.api.getActivityById(id, languageFilter)
    }

    suspend fun getPlaceById(
        id: String,
        languageFilter: String
    ): Response<SingleHelsinkiPlace> {
        return HelsinkiApiRetrofitInstance.api.getPlaceByID(id, languageFilter)
    }

    suspend fun getEventById(
        id: String,
        languageFilter: String
    ): Response<SingleHelsinkiEvent> {
        return HelsinkiApiRetrofitInstance.api.getEventById(id, languageFilter)
    }

}
