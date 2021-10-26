package fi.lauriari.helsinkiapp.network

import fi.lauriari.helsinkiapp.datamodels.HelsinkiActivities
import fi.lauriari.helsinkiapp.datamodels.HelsinkiEvents
import fi.lauriari.helsinkiapp.datamodels.HelsinkiPlaces
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface HelsinkiApi {

    @GET("v1/activities/")
    suspend fun getActivities(
        @Query("tags_search") action: String,
        @Query("language_filter") languageFilter: String,
    ): Response<HelsinkiActivities>

    @GET("v1/places/")
    suspend fun getPlaces(
        @Query("tags_search") action: String,
        @Query("language_filter") languageFilter: String,
    ): Response<HelsinkiPlaces>

    @GET("v1/events/")
    suspend fun getEvents(
        @Query("tags_search") action: String,
        @Query("language_filter") languageFilter: String,
    ): Response<HelsinkiEvents>


    @GET("v1/activities/")
    suspend fun getActivitiesNearby(
        @Query("distance_filter") stringrepresentation: String,
        @Query("language_filter") languageFilter: String
    ): Response<HelsinkiActivities>

    @GET("v1/places/")
    suspend fun getPlacesNearby(
        @Query("distance_filter") stringrepresentation: String,
        @Query("language_filter") languageFilter: String
    ): Response<HelsinkiPlaces>

    @GET("v1/events/")
    suspend fun getEventsNearby(
        @Query("distance_filter") stringrepresentation: String,
        @Query("language_filter") languageFilter: String
    ): Response<HelsinkiEvents>
}