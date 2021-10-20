package fi.lauriari.helsinkiapp.network

import fi.lauriari.helsinkiapp.datamodels.HelsinkiActivities
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface HelsinkiApi {

    @GET("v1/activities/")
    suspend fun getActivities(
        @Query("tags_search") action: String,
        @Query("language_filter") languageFilter: String,
    ): Response<HelsinkiActivities>

    @GET("v1/activities/")
    suspend fun getActivitiesNearby(
        @Query("distance_filter") stringrepresentation: String,
        @Query("language_filter") languageFilter: String
    ): Response<HelsinkiActivities>
}