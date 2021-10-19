package fi.lauriari.helsinkiapp.repositories

import android.util.Log
import fi.lauriari.helsinkiapp.datamodels.HelsinkiActivities
import fi.lauriari.helsinkiapp.network.HelsinkiApiRetrofitInstance
import retrofit2.Response

class HelsinkiApiRepository {

    suspend fun getActivities(tag: String, language: String): Response<HelsinkiActivities> {
        return HelsinkiApiRetrofitInstance.api.getActivities(tag, language)
    }
}