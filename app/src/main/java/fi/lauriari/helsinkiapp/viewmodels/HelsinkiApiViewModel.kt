package fi.lauriari.helsinkiapp.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fi.lauriari.helsinkiapp.datamodels.HelsinkiActivities
import fi.lauriari.helsinkiapp.repositories.HelsinkiApiRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Response

class HelsinkiApiViewModel(private val helsinkiApiRepository: HelsinkiApiRepository) : ViewModel() {

    var response: MutableLiveData<Response<HelsinkiActivities>> = MutableLiveData()

    fun getActivities(tag: String, language: String) {
        viewModelScope.launch {
            val apiResponse = helsinkiApiRepository.getActivities(tag, language)
            response.value = apiResponse
        }
    }
    fun logNice() {
        Log.d("nice", "nice")
    }
}