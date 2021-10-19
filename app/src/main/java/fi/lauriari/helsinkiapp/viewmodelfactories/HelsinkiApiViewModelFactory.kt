package fi.lauriari.helsinkiapp.viewmodelfactories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import fi.lauriari.helsinkiapp.repositories.HelsinkiApiRepository
import fi.lauriari.helsinkiapp.viewmodels.HelsinkiApiViewModel

class HelsinkiApiViewModelFactory(private val repository: HelsinkiApiRepository):ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return HelsinkiApiViewModel(repository) as T
    }
}