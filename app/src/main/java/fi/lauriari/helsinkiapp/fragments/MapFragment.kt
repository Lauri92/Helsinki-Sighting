package fi.lauriari.helsinkiapp.fragments

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.navArgs
import androidx.preference.PreferenceManager
import com.google.android.gms.location.*
import fi.lauriari.helsinkiapp.R
import fi.lauriari.helsinkiapp.databinding.FragmentMapBinding
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.overlay.Marker
import org.osmdroid.bonuspack.routing.OSRMRoadManager
import org.osmdroid.bonuspack.routing.RoadManager
import java.util.ArrayList
import org.osmdroid.bonuspack.routing.Road
import org.osmdroid.views.overlay.Polyline
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.provider.Settings
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class MapFragment : Fragment() {

    private val args by navArgs<MapFragmentArgs>()
    private lateinit var fusedLocationClient:
            FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var locationRequest: LocationRequest? = null
    private var userLocation: GeoPoint? = null
    private lateinit var ownLocationmarker: Marker
    private lateinit var locationmarker: Marker
    private var roadManager: OSRMRoadManager? = null
    private var transportationType: String = "byFoot"

    private var binding: FragmentMapBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_map, container, false)
        binding!!.lifecycleOwner = this
        initLocationClientRequestAndCallback()
        checkSelfPermissions()
        setMap()
        setMarkers()
        setOnClickListerners()

        BottomSheetBehavior.from(binding!!.bottomSheet).apply {
            peekHeight = 65
            this.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        return binding!!.root
    }

    private fun setOnClickListerners() {
        binding!!.myLocationFab.setOnClickListener {
            binding!!.map.controller.animateTo(
                GeoPoint(
                    userLocation!!.latitude,
                    userLocation!!.longitude
                )
            )
        }
        binding!!.itemLocationFab.setOnClickListener {
            binding!!.map.controller.animateTo(
                GeoPoint(
                    args.latitude.toDouble(),
                    args.longitude.toDouble()
                )
            )
        }
        binding!!.clearFab.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Navigation")
            builder.setMessage(
                "Are you sure you want to remove navigation helper?"
            )
            builder.setIcon(android.R.drawable.ic_dialog_alert)
            builder.setPositiveButton("Yes") { _, _ ->
                binding!!.clearFab.isClickable = false
                binding!!.map.overlays.forEach {
                    if ((it is Polyline && it.id == roadPolyline) || (it is Marker && it.id == roadPolylineMarker)) {
                        binding!!.map.overlays.remove(it)
                    }
                }
                binding!!.clearFab.visibility = View.GONE
                binding!!.clearFab.isClickable = true
                binding!!.walkDirectionsActiveIv.visibility = View.GONE
                binding!!.bikeDirectionsActiveIv.visibility = View.GONE
                binding!!.carDirectionsActiveIv.visibility = View.GONE

            }
            builder.setNegativeButton("Cancel") { _, _ ->
            }.create()
            builder.show()
        }
        binding!!.walkDirectionsFab.setOnClickListener {
            transportationType = "byFoot"
            binding!!.walkDirectionsActiveIv.visibility = View.VISIBLE
            binding!!.bikeDirectionsActiveIv.visibility = View.GONE
            binding!!.carDirectionsActiveIv.visibility = View.GONE

            binding!!.map.overlays.forEach {
                if ((it is Polyline && it.id == roadPolyline) || (it is Marker && it.id == roadPolylineMarker)) {
                    binding!!.map.overlays.remove(it)
                }
            }
            binding!!.walkDirectionsFab.isClickable = false
            binding!!.bikeDirectionsFab.isClickable = false
            binding!!.carDirectionsFab.isClickable = false
            lifecycleScope.launch(context = Dispatchers.IO) {
                val addRoute = async(Dispatchers.IO) {
                    createRoadPolyLine()
                }
                addRoute.await()
                activity?.runOnUiThread {
                    binding!!.clearFab.visibility = View.VISIBLE
                    binding!!.walkDirectionsFab.isClickable = true
                    binding!!.bikeDirectionsFab.isClickable = true
                    binding!!.carDirectionsFab.isClickable = true
                }
            }

        }
        binding!!.bikeDirectionsFab.setOnClickListener {
            transportationType = "byBike"
            binding!!.walkDirectionsActiveIv.visibility = View.GONE
            binding!!.bikeDirectionsActiveIv.visibility = View.VISIBLE
            binding!!.carDirectionsActiveIv.visibility = View.GONE
            binding!!.map.overlays.forEach {
                if ((it is Polyline && it.id == roadPolyline) || (it is Marker && it.id == roadPolylineMarker)) {
                    binding!!.map.overlays.remove(it)
                }
            }
            binding!!.walkDirectionsFab.isClickable = false
            binding!!.bikeDirectionsFab.isClickable = false
            binding!!.carDirectionsFab.isClickable = false
            lifecycleScope.launch(context = Dispatchers.IO) {
                val addRoute = async(Dispatchers.IO) {
                    createRoadPolyLine()
                }
                addRoute.await()
                activity?.runOnUiThread {
                    binding!!.clearFab.visibility = View.VISIBLE
                    binding!!.walkDirectionsFab.isClickable = true
                    binding!!.bikeDirectionsFab.isClickable = true
                    binding!!.carDirectionsFab.isClickable = true
                }
            }
        }
        binding!!.carDirectionsFab.setOnClickListener {
            transportationType = "byCar"
            binding!!.walkDirectionsActiveIv.visibility = View.GONE
            binding!!.bikeDirectionsActiveIv.visibility = View.GONE
            binding!!.carDirectionsActiveIv.visibility = View.VISIBLE
            binding!!.map.overlays.forEach {
                if ((it is Polyline && it.id == roadPolyline) || (it is Marker && it.id == roadPolylineMarker)) {
                    binding!!.map.overlays.remove(it)
                }
            }
            binding!!.walkDirectionsFab.isClickable = false
            binding!!.bikeDirectionsFab.isClickable = false
            binding!!.carDirectionsFab.isClickable = false
            lifecycleScope.launch(context = Dispatchers.IO) {
                val addRoute = async(Dispatchers.IO) {
                    createRoadPolyLine()
                }
                addRoute.await()
                activity?.runOnUiThread {
                    binding!!.clearFab.visibility = View.VISIBLE
                    binding!!.walkDirectionsFab.isClickable = true
                    binding!!.bikeDirectionsFab.isClickable = true
                    binding!!.carDirectionsFab.isClickable = true
                }
            }
        }
    }

    private fun createRoadPolyLine() {
        roadManager = OSRMRoadManager(
            requireContext(),
            PreferenceManager.getDefaultSharedPreferences(requireContext()).toString()
        )

        val waypoints = ArrayList<GeoPoint>()
        waypoints.add(GeoPoint(userLocation!!.latitude, userLocation!!.longitude))
        val endPoint = GeoPoint(args.latitude.toDouble(), args.longitude.toDouble())
        waypoints.add(endPoint)

        when (transportationType) {
            "byFoot" -> {
                (roadManager as OSRMRoadManager).setMean(OSRMRoadManager.MEAN_BY_FOOT)
            }
            "byBike" -> {
                (roadManager as OSRMRoadManager).setMean(OSRMRoadManager.MEAN_BY_BIKE)
            }
            else -> {
                (roadManager as OSRMRoadManager).setMean(OSRMRoadManager.MEAN_BY_CAR)
            }
        }

        val road = roadManager!!.getRoad(waypoints)

        val roadOverlay = RoadManager.buildRoadOverlay(road)
        roadOverlay.id = roadPolyline

        binding!!.map.overlays.add(roadOverlay)

        createPolylineMarkers(road)
    }

    private fun createPolylineMarkers(road: Road) {
        for (i in road.mNodes.indices) {
            val node = road.mNodes[i]
            val nodeMarker = Marker(binding!!.map)
            nodeMarker.id = roadPolylineMarker
            nodeMarker.icon = AppCompatResources.getDrawable(
                requireContext(),
                R.drawable.ic_baseline_directions_24
            )
            nodeMarker.position = node.mLocation
            nodeMarker.title = "Step $i"
            nodeMarker.snippet = node.mInstructions
            nodeMarker.subDescription =
                Road.getLengthDurationText(requireContext(), node.mLength, node.mDuration)
            if (node.mInstructions != null) {
                when {
                    node.mInstructions.contains("Turn right") -> {
                        nodeMarker.image = AppCompatResources.getDrawable(
                            requireContext(),
                            R.drawable.ic_turn_right
                        )
                    }
                    node.mInstructions.contains("Turn left") -> {
                        nodeMarker.image = AppCompatResources.getDrawable(
                            requireContext(),
                            R.drawable.ic_turn_left
                        )
                    }
                    node.mInstructions.contains("Continue") -> {
                        nodeMarker.image = AppCompatResources.getDrawable(
                            requireContext(),
                            R.drawable.ic_continue
                        )
                    }
                    node.mInstructions.contains("slight left") -> {
                        nodeMarker.image = AppCompatResources.getDrawable(
                            requireContext(),
                            R.drawable.ic_slight_left
                        )
                    }
                    node.mInstructions.contains("slight right") -> {
                        nodeMarker.image = AppCompatResources.getDrawable(
                            requireContext(),
                            R.drawable.ic_slight_right
                        )
                    }
                    node.mInstructions.contains("waypoint") -> {
                        nodeMarker.image = AppCompatResources.getDrawable(
                            requireContext(),
                            R.drawable.ic_arrived
                        )
                    }
                }
            }
            binding!!.map.overlays.add(nodeMarker)
            binding!!.map.invalidate()
        }
    }

    private fun setMap() {
        binding!!.map.setTileSource(TileSourceFactory.MAPNIK)
        binding!!.map.setMultiTouchControls(true)
        binding!!.map.zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
        binding!!.map.controller.setZoom(16.0)
        binding!!.map.controller.setCenter(
            GeoPoint(
                args.latitude.toDouble(),
                args.longitude.toDouble()
            )
        )
    }

    private fun setMarkers() {

        // Own location marker is used in updating ownlocation but initilized here
        ownLocationmarker = Marker(binding!!.map)

        locationmarker = Marker(binding!!.map).also { marker ->
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            marker.icon = AppCompatResources.getDrawable(
                requireContext(),
                R.drawable.ic_baseline_location_on_24
            )
            marker.position = GeoPoint(
                args.latitude.toDouble(),
                args.longitude.toDouble()
            )
            marker.title = args.itemName
            marker.setOnMarkerClickListener { marker, _ ->
                marker.showInfoWindow()
                return@setOnMarkerClickListener true
            }
        }

        binding!!.map.overlays.add(locationmarker)
    }

    private fun updateUserLocation(geoPoint: GeoPoint) {

        binding!!.map.overlays.forEach {
            if (it is Marker && it.id == ownLocationId) {
                binding!!.map.overlays.remove(it)
            }
        }
        ownLocationmarker.let { marker ->
            marker.id = ownLocationId
            marker.icon = AppCompatResources.getDrawable(
                requireContext(),
                R.drawable.person
            )
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            marker.position = GeoPoint(geoPoint.latitude, geoPoint.longitude)
            marker.setInfoWindow(null)
        }
        binding!!.map.overlays.add(ownLocationmarker)
        //displays the ownLocationmarker as soon as it has been added.
        binding!!.map.invalidate()
    }

    private fun initLocationClientRequestAndCallback() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        //important! set your user agent to prevent getting banned from the osmdroid servers
        Configuration.getInstance().load(
            requireContext(),
            PreferenceManager.getDefaultSharedPreferences(requireContext())
        )

        locationRequest = LocationRequest
            .create()
            .setInterval(1000)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)


        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return

                for (location in locationResult.locations) {
                    Log.d("location", "${location.latitude} ${location.longitude}")
                    val geoPoint = GeoPoint(location.latitude, location.longitude)
                    userLocation = GeoPoint(location.latitude, location.longitude)
                    updateUserLocation(userLocation!!)
                }
            }
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

    companion object {
        private const val PERMISSIONS_REQUEST_LOCATION = 0
        private const val ownLocationId = "ownLocationMarker"
        private const val roadPolyline = "roadPolyline"
        private const val roadPolylineMarker = "roadPolylineMarker"
    }

    override fun onDestroyView() {
        Log.d("destroy", "onDestroyView called")
        super.onDestroyView()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}