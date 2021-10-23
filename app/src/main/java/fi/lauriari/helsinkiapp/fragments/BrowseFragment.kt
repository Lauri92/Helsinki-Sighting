package fi.lauriari.helsinkiapp.fragments

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.*
import fi.lauriari.helsinkiapp.R
import fi.lauriari.helsinkiapp.adapters.ItemsAdapter
import fi.lauriari.helsinkiapp.classes.SingleHelsinkiItem
import fi.lauriari.helsinkiapp.databinding.FragmentBrowseBinding
import fi.lauriari.helsinkiapp.repositories.HelsinkiApiRepository
import fi.lauriari.helsinkiapp.viewmodelfactories.HelsinkiApiViewModelFactory
import fi.lauriari.helsinkiapp.viewmodels.HelsinkiApiViewModel

import fi.lauriari.helsinkiapp.datamodels.HelsinkiActivities
import fi.lauriari.helsinkiapp.datamodels.HelsinkiEvents
import fi.lauriari.helsinkiapp.datamodels.HelsinkiPlaces
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import retrofit2.Response


class BrowseFragment : Fragment() {

    private lateinit var apiViewModel: HelsinkiApiViewModel
    var binding: FragmentBrowseBinding? = null

    private lateinit var fusedLocationClient:
            FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var locationRequest: LocationRequest? = null
    private var userLocation: GeoPoint? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_browse, container, false)
        val view = binding!!.root
        initializeViewModelRepositoryBinding(binding!!)

        initLocationClientRequestAndCallback()

        checkSelfPermissions()

        //setObservers()

        binding?.recyclerview?.adapter?.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY

        setSpinner()

        return view
    }

    private fun initializeViewModelRepositoryBinding(binding: FragmentBrowseBinding) {
        val apiRepository = HelsinkiApiRepository()
        val viewModelFactory = HelsinkiApiViewModelFactory(apiRepository)
        apiViewModel =
            ViewModelProvider(this, viewModelFactory).get(HelsinkiApiViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewmodel = apiViewModel
    }

    private fun initLocationClientRequestAndCallback() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())


        locationRequest = LocationRequest
            .create()
            .setInterval(1000)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)


        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return

                for (location in locationResult.locations) {
                    val geoPoint = GeoPoint(location.latitude, location.longitude)
                    Log.d("location", geoPoint.toDoubleString())
                    userLocation = GeoPoint(location.latitude, location.longitude)
                }
            }
        }
    }

    /*
    // FIXME: called when returning from singleItemFragment
    private fun setObservers() {
        binding?.viewmodel?.activitiesResponse?.observe(viewLifecycleOwner, { response ->
            Log.d("observers", "activitiesResponse observer")
            handleActivitiesResponse(response)
            Log.d("observers", "activitiesResponse value :${response.body()!!.meta}")
        })
        binding?.viewmodel?.placesResponse?.observe(viewLifecycleOwner, { response ->
            Log.d("observers", "placesResponse observer")
            handlePlacesResponse(response)
            Log.d("observers", "placesResponse value :${response.body()!!.meta}")
        })
        binding?.viewmodel?.eventsResponse?.observe(viewLifecycleOwner, { response ->
            Log.d("observers", "eventsResponse observer")
            handleEventsResponse(response)
            Log.d("observers", "eventsResponse value :${response.body()!!.meta}")
        })
    }

     */

    private fun setSpinner() {
        val spinnerArray = listOf("Select", "Activities", "Places", "Events")

        ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            spinnerArray
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding?.spinner?.adapter = adapter
        }
        binding?.spinner?.onItemSelectedListener = ItemsSpinner()
    }

    private fun handleActivitiesResponse(response: Response<HelsinkiActivities>) {
        if (response.isSuccessful) {
            binding?.recyclerview?.layoutManager =
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
                        streetAddress = it.location.address.street_address,
                        locality = it.location.address.locality,
                        description = it.description.body,
                        images = it.description.images,
                        tags = it.tags,
                        whereWhenDuration = it.where_when_duration
                    )
                )
            }
            binding?.recyclerview?.adapter =
                ItemsAdapter(adapterList, requireContext())
            binding?.progressBar?.visibility = View.GONE
        } else {
            // TODO Maybe create an alert dialog showing that the fetch failed
            Toast.makeText(requireContext(), "Fail fetching items", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handlePlacesResponse(response: Response<HelsinkiPlaces>) {
        if (response.isSuccessful) {
            binding?.recyclerview?.layoutManager =
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
                        streetAddress = it.location.address.street_address,
                        locality = it.location.address.locality,
                        description = it.description.body,
                        images = it.description.images,
                        tags = it.tags,
                        openingHours = it.opening_hours
                    )
                )
            }
            binding?.recyclerview?.adapter =
                ItemsAdapter(adapterList, requireContext())
            binding?.progressBar?.visibility = View.GONE
        } else {
            // TODO Maybe create an alert dialog showing that the fetch failed
            Toast.makeText(requireContext(), "Fail fetching items", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleEventsResponse(response: Response<HelsinkiEvents>) {
        if (response.isSuccessful) {
            binding?.recyclerview?.layoutManager =
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
                        streetAddress = it.location.address.street_address,
                        locality = it.location.address.locality,
                        description = it.description.body,
                        images = it.description.images,
                        tags = it.tags,
                        eventDates = it.event_dates
                    )
                )
            }
            binding?.recyclerview?.adapter =
                ItemsAdapter(adapterList, requireContext())
            binding?.progressBar?.visibility = View.GONE
        } else {
            // TODO Maybe create an alert dialog showing that the fetch failed
            Toast.makeText(requireContext(), "Fail fetching items", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkSelfPermissions() {

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Permissions are not granted so requesting of location updates is not possible
            // Prompt user to grant them then listen to callback whether they were granted or not
            Log.i("test", "failed")
            AlertDialog.Builder(requireContext())
                .setTitle("Location Permission required")
                .setMessage("This application needs the Location permission, please accept to use location functionality")
                .setPositiveButton(
                    "OK"
                ) { _, _ ->
                    //Prompt the user once explanation has been shown
                    // FIXME: deprecation
                    requestPermissions(
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        PERMISSIONS_REQUEST_LOCATION
                    )
                }
                .create()
                .show()

        } else {
            // Permissions are granted so start requesting location updates
            Log.i("test", "Passed")

            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        Log.d("locations", "Got last known location.")
                        userLocation = GeoPoint(location.latitude, location.longitude)
                    }
                }
            fusedLocationClient.requestLocationUpdates(
                locationRequest!!,
                locationCallback, Looper.getMainLooper()
            )

        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSIONS_REQUEST_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    if (ContextCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        fusedLocationClient.requestLocationUpdates(
                            locationRequest!!,
                            locationCallback, Looper.getMainLooper()
                        )

                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        Log.d("destroy", "onDestroyView called")
        super.onDestroyView()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    inner class ItemsSpinner : Fragment(), AdapterView.OnItemSelectedListener {

        override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
            if (view != null) {
                when (parent.getItemAtPosition(pos).toString()) {
                    "Activities" -> {
                        Log.d("response", "Activities spinner")
                        if (userLocation != null) {
                            binding?.progressBar?.visibility = View.VISIBLE
                            lifecycleScope.launch(context = Dispatchers.IO) {
                                val getActivities = async {
                                    binding?.viewmodel?.getActivitiesNearby(
                                        /*
                                Triple(
                                    userLocation!!.latitude, userLocation!!.longitude, 0.5
                                ),*/Triple(60.1700713, 24.9532164, 0.5),
                                        "en"
                                    )
                                }
                                getActivities.await()?.let {
                                    lifecycleScope.launch(context = Dispatchers.Main) {
                                        handleActivitiesResponse(it)
                                    }
                                }
                            }
                        }
                    }
                    "Places" -> {
                        Log.d("response", "Places spinner")
                        if (userLocation != null) {
                            binding?.progressBar?.visibility = View.VISIBLE
                            lifecycleScope.launch(context = Dispatchers.IO) {
                                val getPlaces = async {
                                    binding?.viewmodel?.getPlacesNearby(
                                        /*
                                Triple(
                                    userLocation!!.latitude, userLocation!!.longitude, 0.5
                                ),*/Triple(60.1700713, 24.9532164, 0.5),
                                        "en"
                                    )
                                }
                                getPlaces.await()?.let {
                                    lifecycleScope.launch(context = Dispatchers.Main) {
                                        handlePlacesResponse(it)
                                    }
                                }
                            }
                        }
                    }
                    "Events" -> {
                        Log.d("response", "Events spinner")
                        if (userLocation != null) {
                            binding?.progressBar?.visibility = View.VISIBLE
                            lifecycleScope.launch(context = Dispatchers.IO) {
                                val getEvents = async {
                                    binding?.viewmodel?.getEventsNearby(
                                        /*
                                Triple(
                                    userLocation!!.latitude, userLocation!!.longitude, 0.5
                                ),*/Triple(60.1700713, 24.9532164, 0.5),
                                        "en"
                                    )
                                }
                                getEvents.await()?.let {
                                    lifecycleScope.launch(context = Dispatchers.Main) {
                                        handleEventsResponse(it)
                                    }
                                }
                            }
                        }
                    }
                    "Select" -> {
                        Log.d("location", "User location: $userLocation")
                    }
                }
            }
        }

        override fun onNothingSelected(parent: AdapterView<*>) {
            // Another interface callback
        }
    }

    companion object {
        private const val PERMISSIONS_REQUEST_LOCATION = 0
    }
}