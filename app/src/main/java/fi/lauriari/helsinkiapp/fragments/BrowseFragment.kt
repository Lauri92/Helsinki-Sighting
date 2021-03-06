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
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import fi.lauriari.helsinkiapp.MainActivity
import fi.lauriari.helsinkiapp.R
import fi.lauriari.helsinkiapp.adapters.ItemsAdapter
import fi.lauriari.helsinkiapp.classes.SingleHelsinkiItem
import fi.lauriari.helsinkiapp.databinding.FragmentBrowseBinding
import fi.lauriari.helsinkiapp.datamodels.HelsinkiActivities
import fi.lauriari.helsinkiapp.datamodels.HelsinkiEvents
import fi.lauriari.helsinkiapp.datamodels.HelsinkiPlaces
import org.osmdroid.util.GeoPoint
import retrofit2.Response
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.android.material.bottomsheet.BottomSheetBehavior
import fi.lauriari.helsinkiapp.viewmodels.BrowseViewModel


class BrowseFragment : Fragment() {

    private lateinit var binding: FragmentBrowseBinding
    private lateinit var layoutManager: LinearLayoutManager
    private val browseViewModel: BrowseViewModel by viewModels()
    private val itemsAdapter: ItemsAdapter by lazy { ItemsAdapter("browseFragment") }
    private lateinit var fusedLocationClient:
            FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var locationRequest: LocationRequest? = null
    private var clickRequest = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_browse, container, false)
        val view = binding.root
        initializeViewModelRepositoryBinding(binding)
        initNavigation()
        initLocationClientRequestAndCallback()
        initSetOnClickListeners()
        checkSelfPermissions()
        initBottomsheetAndSlider()
        setObservers()

        return view
    }

    private fun initBottomsheetAndSlider() {
        BottomSheetBehavior.from(binding.bottomSheet).apply {
            peekHeight = 60
            this.state = BottomSheetBehavior.STATE_COLLAPSED
        }
        binding.viewmodel?.range?.value = 0.5
        binding.slider.value = 500F
        binding.rangeTv.text = 500F.toInt().toString() + "m"

        binding.slider.addOnChangeListener { slider, value, fromUser ->
            binding.viewmodel?.range?.value = (slider.value / 1000).toDouble()
            binding.rangeTv.text = "${slider.value.toInt()}m"
        }
    }

    private fun initNavigation() {
        (requireActivity() as MainActivity).findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility =
            View.VISIBLE
        (requireActivity() as MainActivity).findViewById<BottomNavigationView>(R.id.bottomNavigationView)
            .setOnItemSelectedListener {
                when (it.itemId) {
                    R.id.near_me -> {
                        Toast.makeText(requireContext(), "You are here", Toast.LENGTH_SHORT).show()
                    }
                    R.id.search -> {
                        findNavController().navigate(R.id.action_browseFragment_to_searchFragment)
                    }
                    R.id.favorites -> {
                        findNavController().navigate(R.id.action_browseFragment_to_favoritesFragment)
                    }

                }
                true

            }
    }

    private fun disableActivateButtons() {
        binding.activitiesButton.isClickable = !binding.activitiesButton.isClickable
        binding.placesButton.isClickable = !binding.placesButton.isClickable
        binding.eventsButton.isClickable = !binding.eventsButton.isClickable
    }

    private fun initSetOnClickListeners() {
        binding.activitiesButton.setOnClickListener {
            binding.viewmodel?.range?.value ?: return@setOnClickListener
            binding.viewmodel?.userLocation?.value ?: return@setOnClickListener

            clickRequest = true
            binding.progressBar.visibility = View.VISIBLE
            binding.viewmodel?.getActivitiesNearby(
                Triple(
                    binding.viewmodel?.userLocation?.value!!.latitude,
                    binding.viewmodel?.userLocation?.value!!.longitude,
                    binding.viewmodel?.range?.value!!
                ), "en"
            )
        }
        binding.placesButton.setOnClickListener {
            binding.viewmodel?.range?.value ?: return@setOnClickListener
            binding.viewmodel?.userLocation?.value ?: return@setOnClickListener

            clickRequest = true
            binding.progressBar.visibility = View.VISIBLE
            binding.viewmodel?.getPlacesNearby(
                Triple(
                    binding.viewmodel?.userLocation?.value!!.latitude,
                    binding.viewmodel?.userLocation?.value!!.longitude,
                    binding.viewmodel?.range?.value!!
                ), "en"
            )
        }
        binding.eventsButton.setOnClickListener {
            binding.viewmodel?.range?.value ?: return@setOnClickListener
            binding.viewmodel?.userLocation?.value ?: return@setOnClickListener

            clickRequest = true
            binding.progressBar.visibility = View.VISIBLE
            binding.viewmodel?.getEventsNearby(
                Triple(
                    binding.viewmodel?.userLocation?.value!!.latitude,
                    binding.viewmodel?.userLocation?.value!!.longitude,
                    binding.viewmodel?.range?.value!!
                ), "en"
            )
        }
    }

    private fun initializeViewModelRepositoryBinding(binding: FragmentBrowseBinding) {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewmodel = browseViewModel
        layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerview.layoutManager = layoutManager
        binding.recyclerview.adapter = itemsAdapter
        val dividerItemDecoration = DividerItemDecoration(
            context,
            layoutManager.orientation
        )
        binding.recyclerview.addItemDecoration(dividerItemDecoration)
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
                    binding.viewmodel?.userLocation?.value =
                        GeoPoint(location.latitude, location.longitude)
                }
            }
        }
    }

    private fun setObservers() {
        binding.viewmodel?.activitiesResponse?.observe(viewLifecycleOwner, { response ->
            Log.d("response", "in activities observer")
            if (clickRequest) {
                disableActivateButtons()
                handleActivitiesResponse(response)
                layoutManager.scrollToPositionWithOffset(0, 0)
                disableActivateButtons()
                clickRequest = false
            }
        })

        binding.viewmodel?.placesResponse?.observe(viewLifecycleOwner, { response ->
            Log.d("response", "in places observer")
            if (clickRequest) {
                disableActivateButtons()
                handlePlacesResponse(response)
                layoutManager.scrollToPositionWithOffset(0, 0)
                disableActivateButtons()
                clickRequest = false
            }
        })
        binding.viewmodel?.eventsResponse?.observe(viewLifecycleOwner, { response ->
            Log.d("response", "in events observer")
            if (clickRequest) {
                disableActivateButtons()
                handleEventsResponse(response)
                layoutManager.scrollToPositionWithOffset(0, 0)
                disableActivateButtons()
                clickRequest = false
            }
        })
    }

    private fun handleActivitiesResponse(response: Response<HelsinkiActivities>) {
        if (response.isSuccessful && response.body()?.data?.isNotEmpty() == true) {
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
                        whereWhenDuration = it.where_when_duration,
                        itemType = it.source_type.name
                    )
                )
            }
            itemsAdapter.setData(adapterList)
            binding.progressBar.visibility = View.GONE
        } else {
            // TODO Maybe create an alert dialog showing that the fetch failed
            showNoEntriesFoundDialog()
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun handlePlacesResponse(response: Response<HelsinkiPlaces>) {
        if (response.isSuccessful && response.body()?.data?.isNotEmpty() == true) {
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
                        openingHours = it.opening_hours,
                        itemType = it.source_type.name
                    )
                )
            }
            itemsAdapter.setData(adapterList)
            binding.progressBar.visibility = View.GONE
        } else {
            // TODO Maybe create an alert dialog showing that the fetch failed
            showNoEntriesFoundDialog()
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun handleEventsResponse(response: Response<HelsinkiEvents>) {
        if (response.isSuccessful && response.body()?.data?.isNotEmpty() == true) {
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
                        eventDates = it.event_dates,
                        itemType = it.source_type.name
                    )
                )
            }
            itemsAdapter.setData(adapterList)
            binding.progressBar.visibility = View.GONE
        } else {
            // TODO Maybe create an alert dialog showing that the fetch failed
            showNoEntriesFoundDialog()
            binding.progressBar.visibility = View.GONE
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
                        binding.viewmodel?.userLocation?.value =
                            GeoPoint(location.latitude, location.longitude)
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

    private fun showNoEntriesFoundDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("No entries found")
        builder.setMessage(
            "Try increasing the radius.\nBeing near Helsinki helps too."
        )
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        builder.setPositiveButton("OK") { _, _ ->
        }.create()
        builder.show()
    }

    companion object {
        private const val PERMISSIONS_REQUEST_LOCATION = 0
    }
}