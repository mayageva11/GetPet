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
import com.getpet.Model.ModelFireBase.Post
import com.getpet.R
import com.getpet.activities.SinglePostActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
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
            fusedLocationClient.lastLocation
                .addOnSuccessListener(requireActivity()) { location ->
                    if (location != null) {
                        Log.d("location", "${location.longitude}########${location.latitude}")
                        latitude = location.latitude
                        longitude = location.longitude

                        // Move camera to the user's current location
                        googleMap.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(location.latitude, location.longitude),
                                13f
                            )
                        )

                        // Add a marker at the user's current location
                        googleMap.addMarker(
                            MarkerOptions()
                                .position(LatLng(location.latitude, location.longitude))
                                .title("Your Location")
                                .snippet("This is your current location")
                        )
                    }
                }
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
