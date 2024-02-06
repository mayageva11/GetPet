package com.getpet.Fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.getpet.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions


class MapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap
    private val MY_PERMISSIONS_REQUEST_LOCATION = 122
    private val MY_PERMISSIONS_REQUEST_FINE_LOCATION = 123

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    private var key: String? = null
    private var isClicked = false

    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    private val userMarkers: MutableMap<String, Marker?> = HashMap()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_map, container, false)

        mapView = rootView.findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        return rootView
    }


//    private fun showSinglePost(postObject: Post) {
//        val singlePostActivity = SinglePostActivity()
//
//        // Create a Bundle and put the Post object into it
//        val bundle = Bundle()
//        bundle.putParcelable("postObject", postObject)
//
//        // Set the arguments for the BottomSheetFragment
//        singlePostActivity.setArguments(bundle)
//
//        // Show the BottomSheetFragment
//        singlePostActivity.show(parentFragmentManager, singlePostActivity.getTag())
//    }


    override fun onMapReady(map: GoogleMap) {
        googleMap = map


        googleMap.setOnMarkerClickListener(this)

        // Check if location permissions are granted
        if (checkLocationPermission()) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

            // Initialize location request
            locationRequest = LocationRequest.create().apply {
                interval = 10000 // Update interval in milliseconds
                fastestInterval = 5000 // Fastest update interval in milliseconds
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }

            // Initialize location callback
            locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    val location = locationResult.lastLocation
                    location?.let {
                        Log.d("location", "${it.longitude}########${it.latitude}")

                        // Update the user's current location
                        updateUserLocation("userId", LatLng(it.latitude, it.longitude))
                    }
                }
            }

            // Request location updates
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
        } else {
            // Request location permissions if not granted.
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                MY_PERMISSIONS_REQUEST_LOCATION
            )
        }
    }

    private fun updateUserLocation(userId: String, location: LatLng) {
        // Check if the marker for the user already exists
        if (userMarkers.containsKey(userId)) {
            // Update the existing marker
            userMarkers[userId]?.position = location
        } else {
            // Add a new marker for the user
            val marker = googleMap.addMarker(
                MarkerOptions()
                    .position(location)
                    .title("User $userId")
                    .snippet("This is the current location of User $userId")
            )

            // Store the marker in the map
            userMarkers[userId] = marker
        }

//        val markers = ArrayList<Marker>()
//        for (post in posts) {
//            val marker = googleMap.addMarker(MarkerOptions().position(post.loct).title(post.kind))
//            markers.add(marker)
//        }

    }


    // Add a marker in a location of your choice and move the camera
//        val location = LatLng( 32.08827676331894, 34.78988217301541)
//        googleMap.addMarker(MarkerOptions().position(location).title("Marker in Tel-Aviv"))
//        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15.0f))

    private fun checkLocationPermission(): Boolean {
        val fineLocationPermission = ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        val coarseLocationPermission = ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        return fineLocationPermission == PackageManager.PERMISSION_GRANTED && coarseLocationPermission == PackageManager.PERMISSION_GRANTED
//
//        // Add a marker in a location of your choice and move the camera
//        val location = LatLng(37.7749, -122.4194) // Example: San Francisco, CA
//        googleMap.addMarker(MarkerOptions().position(location).title("Marker in Tel-Aviv"))
//        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 12.0f))
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }


    override fun onMarkerClick(marker: Marker): Boolean {
//        Log.d("YES", "IN MAER")
//        for (post in posts) {
//            if (post.kind == marker.title) {
//                // Found the corresponding Shiva object
//                (post)
//                break // No need to continue the loop
//            }
//        }
        return true

    }
}
