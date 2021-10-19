package fi.lauriari.helsinkiapp.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import fi.lauriari.helsinkiapp.R
import fi.lauriari.helsinkiapp.adapters.ActivitiesAdapter
import fi.lauriari.helsinkiapp.classes.SingleHelsinkiActivity
import fi.lauriari.helsinkiapp.databinding.FragmentBrowseBinding
import fi.lauriari.helsinkiapp.repositories.HelsinkiApiRepository
import fi.lauriari.helsinkiapp.viewmodelfactories.HelsinkiApiViewModelFactory
import fi.lauriari.helsinkiapp.viewmodels.HelsinkiApiViewModel

class BrowseFragment : Fragment() {

    private lateinit var apiViewModel: HelsinkiApiViewModel
    var accessBinding: FragmentBrowseBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentBrowseBinding =
            DataBindingUtil.inflate(
                inflater, R.layout.fragment_browse,
                container, false
            )
        val view = binding.root
        initializeViewModelRepositoryBinding(binding)

        accessBinding?.viewmodel?.getActivities("guidance", "en")

        accessBinding?.viewmodel?.response?.observe(viewLifecycleOwner, { response ->
            if (response.isSuccessful) {
                Log.d("response", "${response.body()!!.data}")
                accessBinding?.recyclerview?.layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                val adapterList = mutableListOf<SingleHelsinkiActivity>()
                response.body()?.data?.forEach {
                    adapterList.add(
                        SingleHelsinkiActivity(
                            id = it.id,
                            name = it.name.en,
                            infoUrl = it.info_url,
                            latitude = it.location.lat,
                            longitude = it.location.lon,
                            locality = it.location.address.locality,
                            description = it.description.body,
                            images = it.description.images,
                            tags = it.tags
                        )
                    )
                    accessBinding?.recyclerview?.adapter =
                        ActivitiesAdapter(adapterList, requireContext())
                }

            } else {
                Toast.makeText(requireContext(), "Fail fetching items", Toast.LENGTH_SHORT).show()
            }
        })


        return view
    }

    private fun initializeViewModelRepositoryBinding(binding: FragmentBrowseBinding) {
        val apiRepository = HelsinkiApiRepository()
        val viewModelFactory = HelsinkiApiViewModelFactory(apiRepository)
        apiViewModel =
            ViewModelProvider(this, viewModelFactory).get(HelsinkiApiViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewmodel = apiViewModel
        accessBinding = binding
    }
}