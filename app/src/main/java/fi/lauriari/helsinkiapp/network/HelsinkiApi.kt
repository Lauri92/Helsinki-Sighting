package fi.lauriari.helsinkiapp.network

import fi.lauriari.helsinkiapp.datamodels.*
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface HelsinkiApi {

    /**
     * Get all activities with matching tag
     */
    @GET("v1/activities/")
    suspend fun getActivities(
        @Query("tags_search") action: String,
        @Query("language_filter") languageFilter: String,
    ): Response<HelsinkiActivities>

    /**
     * Get all places with matching tag
     */
    @GET("v1/places/")
    suspend fun getPlaces(
        @Query("tags_search") action: String,
        @Query("language_filter") languageFilter: String,
    ): Response<HelsinkiPlaces>

    /**
     * Get all events with matching tag
     */
    @GET("v1/events/")
    suspend fun getEvents(
        @Query("tags_search") action: String,
        @Query("language_filter") languageFilter: String,
    ): Response<HelsinkiEvents>

    /**
     * Get all activities nearby
     */
    @GET("v1/activities/")
    suspend fun getActivitiesNearby(
        @Query("distance_filter") stringrepresentation: String,
        @Query("language_filter") languageFilter: String
    ): Response<HelsinkiActivities>

    /**
     * Get all places nearby
     */
    @GET("v1/places/")
    suspend fun getPlacesNearby(
        @Query("distance_filter") stringrepresentation: String,
        @Query("language_filter") languageFilter: String
    ): Response<HelsinkiPlaces>

    /**
     * Get all events nearby
     */
    @GET("v1/events/")
    suspend fun getEventsNearby(
        @Query("distance_filter") stringrepresentation: String,
        @Query("language_filter") languageFilter: String
    ): Response<HelsinkiEvents>

    /**
     * Get single activity by it's ID
     */
    @GET("v1/activity/{activity_id}")
    suspend fun getActivityById(
        @Path(value = "activity_id", encoded = true) activityId: String,
        @Query("language_filter") languageFilter: String,
    ): Response<SingleHelsinkiActivity>

    /**
     * Get single place by it's ID
     */
    @GET("v1/place/{place_id}")
    suspend fun getPlaceByID(
        @Path(value = "place_id", encoded = true) activityId: String,
        @Query("language_filter") languageFilter: String,
    ): Response<SingleHelsinkiPlace>

}