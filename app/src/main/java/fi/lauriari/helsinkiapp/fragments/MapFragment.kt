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

class MapFragment : Fragment() {

    private val args by navArgs<MapFragmentArgs>()
    private lateinit var fusedLocationClient:
            FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var locationRequest: LocationRequest? = null
    private var userLocation: GeoPoint? = null

    private var binding: FragmentMapBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_map, container, false)
        binding!!.lifecycleOwner = this
        initLocationClientRequestAndCallback()
        checkSelfPermissions()
        setMapAndMarker()

        return binding!!.root
    }

    private fun setMapAndMarker() {
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
        val marker = Marker(binding!!.map)
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        marker.icon = AppCompatResources.getDrawable(
            requireContext(),
            R.drawable.ic_baseline_location_on_24
        )
        marker.position = GeoPoint(
            args.latitude.toDouble(),
            args.longitude.toDouble()
        )

        marker.setOnMarkerClickListener { _, _ ->
            Toast.makeText(requireContext(), "Clicked marker", Toast.LENGTH_SHORT).show()
            return@setOnMarkerClickListener true
        }

        binding!!.map.overlays.add(marker)

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
                    val geoPoint = GeoPoint(location.latitude, location.longitude)
                    Log.d("location", geoPoint.toDoubleString())
                    userLocation = GeoPoint(location.latitude, location.longitude)
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
    }

    override fun onDestroyView() {
        Log.d("destroy", "onDestroyView called")
        super.onDestroyView()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}