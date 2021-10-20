package fi.lauriari.helsinkiapp.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import fi.lauriari.helsinkiapp.R
import fi.lauriari.helsinkiapp.adapters.ActivitiesAdapter
import fi.lauriari.helsinkiapp.classes.SingleHelsinkiItem
import fi.lauriari.helsinkiapp.databinding.FragmentBrowseBinding
import fi.lauriari.helsinkiapp.repositories.HelsinkiApiRepository
import fi.lauriari.helsinkiapp.viewmodelfactories.HelsinkiApiViewModelFactory
import fi.lauriari.helsinkiapp.viewmodels.HelsinkiApiViewModel

import fi.lauriari.helsinkiapp.datamodels.HelsinkiActivities
import fi.lauriari.helsinkiapp.datamodels.HelsinkiEvents
import fi.lauriari.helsinkiapp.datamodels.HelsinkiPlaces
import retrofit2.Response


class BrowseFragment : Fragment() {

    private lateinit var apiViewModel: HelsinkiApiViewModel
    var accessBinding: FragmentBrowseBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentBrowseBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_browse, container, false)
        val view = binding.root
        initializeViewModelRepositoryBinding(binding)

        setObservers()

        setSpinner()

        return view
    }

    private fun setObservers() {
        accessBinding?.viewmodel?.activitiesResponse?.observe(viewLifecycleOwner, { response ->
            handleActivitiesResponse(response)
        })
        accessBinding?.viewmodel?.placesResponse?.observe(viewLifecycleOwner, { response ->
            handlePlacesResponse(response)
        })

        accessBinding?.viewmodel?.eventsResponse?.observe(viewLifecycleOwner, { response ->
            handleEventsResponse(response)
        })
    }

    private fun setSpinner() {
        val spinnerArray = listOf("Activities", "Places", "Events")

        ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            spinnerArray
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            accessBinding?.spinner?.adapter = adapter
        }
        accessBinding?.spinner?.onItemSelectedListener = SpinnerActivity()
    }

    private fun handleActivitiesResponse(response: Response<HelsinkiActivities>?) {
        if (response!!.isSuccessful) {
            Log.d("response", "${response.body()!!.data}")
            accessBinding?.recyclerview?.layoutManager =
                LinearLayoutManager(requireContext())
            val adapterList = mutableListOf<SingleHelsinkiItem>()
            response.body()?.data?.forEach {
                adapterList.add(
                    SingleHelsinkiItem(
                        id = it.id,
                        name = it.name.en,
                        infoUrl = it.info_url,
                        latitude = it.location.lat,
                        longitude = it.location.lon,
                        locality = it.location.address.locality,
                        description = it.description.body,
                        images = it.description.images,
                        tags = it.tags,
                        whereWhenDuration = it.where_when_duration
                    )
                )
            }
            accessBinding?.recyclerview?.adapter =
                ActivitiesAdapter(adapterList, requireContext())
        } else {
            // TODO Maybe create an alert dialog showing that the fetch failed
            Toast.makeText(requireContext(), "Fail fetching items", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handlePlacesResponse(response: Response<HelsinkiPlaces>?) {
        if (response!!.isSuccessful) {
            Log.d("response", "${response.body()!!.data}")
            accessBinding?.recyclerview?.layoutManager =
                LinearLayoutManager(requireContext())
            val adapterList = mutableListOf<SingleHelsinkiItem>()
            response.body()?.data?.forEach {
                adapterList.add(
                    SingleHelsinkiItem(
                        id = it.id,
                        name = it.name.en,
                        infoUrl = it.info_url,
                        latitude = it.location.lat,
                        longitude = it.location.lon,
                        locality = it.location.address.locality,
                        description = it.description.body,
                        images = it.description.images,
                        tags = it.tags,
                        openingHours = it.opening_hours
                    )
                )
            }
            accessBinding?.recyclerview?.adapter =
                ActivitiesAdapter(adapterList, requireContext())
        } else {
            // TODO Maybe create an alert dialog showing that the fetch failed
            Toast.makeText(requireContext(), "Fail fetching items", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleEventsResponse(response: Response<HelsinkiEvents>) {
        if (response.isSuccessful) {
            Log.d("response", "${response.body()!!.data}")
            accessBinding?.recyclerview?.layoutManager =
                LinearLayoutManager(requireContext())
            val adapterList = mutableListOf<SingleHelsinkiItem>()
            response.body()?.data?.forEach {
                adapterList.add(
                    SingleHelsinkiItem(
                        id = it.id,
                        name = it.name.en,
                        infoUrl = it.info_url,
                        latitude = it.location.lat,
                        longitude = it.location.lon,
                        locality = it.location.address.locality,
                        description = it.description.body,
                        images = it.description.images,
                        tags = it.tags,
                        eventDates = it.event_dates
                    )
                )
            }
            accessBinding?.recyclerview?.adapter =
                ActivitiesAdapter(adapterList, requireContext())
        } else {
            // TODO Maybe create an alert dialog showing that the fetch failed
            Toast.makeText(requireContext(), "Fail fetching items", Toast.LENGTH_SHORT).show()
        }
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

    inner class SpinnerActivity : Fragment(), AdapterView.OnItemSelectedListener {

        override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
            Log.d("spinner", parent.getItemAtPosition(pos).toString())
            when (parent.getItemAtPosition(pos).toString()) {
                "Activities" -> {
                    accessBinding?.viewmodel?.getActivitiesNearby(
                        Triple(
                            60.16884994506836, 24.94088363647461, 0.2
                        ),
                        "en"
                    )
                }
                "Places" -> {
                    accessBinding?.viewmodel?.getPlacesNearby(
                        Triple(
                            60.16884994506836, 24.94088363647461, 0.2
                        ),
                        "en"
                    )
                }
                "Events" -> {
                    accessBinding?.viewmodel?.getEventsNearby(
                        Triple(
                            60.16884994506836, 24.94088363647461, 0.2
                        ),
                        "en"
                    )
                }
            }
        }

        override fun onNothingSelected(parent: AdapterView<*>) {
            // Another interface callback
        }
    }
}