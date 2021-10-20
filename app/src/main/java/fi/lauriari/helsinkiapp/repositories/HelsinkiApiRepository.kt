package fi.lauriari.helsinkiapp.repositories

import android.util.Log
import fi.lauriari.helsinkiapp.datamodels.HelsinkiActivities
import fi.lauriari.helsinkiapp.datamodels.HelsinkiEvents
import fi.lauriari.helsinkiapp.datamodels.HelsinkiPlaces
import fi.lauriari.helsinkiapp.network.HelsinkiApiRetrofitInstance
import retrofit2.Response

class HelsinkiApiRepository {

    suspend fun getActivities(tag: String, language: String): Response<HelsinkiActivities> {
        return HelsinkiApiRetrofitInstance.api.getActivities(tag, language)
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


}
