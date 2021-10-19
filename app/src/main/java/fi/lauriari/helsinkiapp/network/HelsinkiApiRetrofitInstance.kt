package fi.lauriari.helsinkiapp.network

import fi.lauriari.helsinkiapp.utils.Constants.Companion.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object HelsinkiApiRetrofitInstance {

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: HelsinkiApi by lazy {
        retrofit.create(HelsinkiApi::class.java)
    }
}