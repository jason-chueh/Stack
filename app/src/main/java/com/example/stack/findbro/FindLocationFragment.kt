package com.example.stack.findbro

import android.os.Bundle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import android.Manifest
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.stack.NavigationDirections
import com.example.stack.R
import com.example.stack.databinding.FragmentFindLocationBinding
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


@AndroidEntryPoint
class FindLocationFragment() : Fragment(), OnMapReadyCallback {
    lateinit var binding: FragmentFindLocationBinding
    lateinit var mMap: GoogleMap
    private lateinit var placesClient: PlacesClient
    private val viewModel: FindLocationViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        if (!Places.isInitialized()) {
            // Places SDK is not initialized, so initialize it
            Places.initialize(this.requireActivity(), com.example.stack.BuildConfig.MAPS_API_KEY)
        }

        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var userLat: String = ""
        var userLong: String = ""

        binding = FragmentFindLocationBinding.inflate(inflater, container, false)
        // Create a new PlacesClient instance
        placesClient = Places.createClient(this.requireContext())
        checkPermissionThenFindCurrentPlace()
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.complete.setOnClickListener {
            if(userLat != "" && userLong != ""){
                viewModel.upsertUser(userLat,userLong)
                findNavController().navigate(NavigationDirections.navigateToMapsFragment())
            }
            else{
                Toast.makeText(
                    this.context,
                    "Register your gym to connect with other users",
                    Toast.LENGTH_SHORT,
                ).show()
            }
        }

        val autocompleteFragment =
            childFragmentManager.findFragmentById(R.id.place_autocomplete_fragment) as AutocompleteSupportFragment?
        autocompleteFragment!!.setPlaceFields(
            listOf(
                Place.Field.ID,
                Place.Field.ADDRESS,
                Place.Field.LAT_LNG
            )
        )
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onError(p0: Status) {
                Log.i("googleMap", "${p0}")
            }

            override fun onPlaceSelected(place: Place) {
                place.address.toString()
                mMap.addMarker(MarkerOptions().position(place.latLng).title(place.name))
                mMap.moveCamera(CameraUpdateFactory.newLatLng(place.latLng))
                mMap.animateCamera(CameraUpdateFactory.zoomTo(20f), 2000, null)
                Log.i("googleMap", "${place}")
                userLat = place.latLng.latitude.toString()
                userLong = place.latLng.longitude.toString()

            }
        })
        return binding.root
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

    }

    private fun checkPermissionThenFindCurrentPlace() {
        when {
            (ContextCompat.checkSelfPermission(
                this.requireContext(),
                ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this.requireContext(),
                ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED) -> {
                // You can use the API that requires the permission.
                findCurrentPlace()
            }
            shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)
            -> {
                Log.d(TAG, "Showing permission rationale dialog")
                // TODO: In an educational UI, explain to the user why your app requires this
                // permission for a specific feature to behave as expected. In this UI,
                // include a "cancel" or "no thanks" button that allows the user to
                // continue using your app without granting the permission.
            }
            else -> {
                // Ask for both the ACCESS_FINE_LOCATION and ACCESS_COARSE_LOCATION permissions.
                ActivityCompat.requestPermissions(
                    this.requireActivity(),
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ),
                    PERMISSION_REQUEST_CODE
                )
            }
        }
    }
    companion object {
        private val TAG = "CurrentPlaceActivity"
        private const val PERMISSION_REQUEST_CODE = 9
    }
    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        if (requestCode != PERMISSION_REQUEST_CODE) {
            super.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults
            )
            return
        } else if (permissions.toList().zip(grantResults.toList())
                .firstOrNull { (permission, grantResult) ->
                    grantResult == PackageManager.PERMISSION_GRANTED && (permission == ACCESS_FINE_LOCATION || permission == ACCESS_COARSE_LOCATION)
                } != null
        )
        // At least one location permission has been granted, so proceed with Find Current Place
            findCurrentPlace()
    }
    @RequiresPermission(anyOf = [ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION])
    private fun findCurrentPlace() {
        // Use fields to define the data types to return.
        val placeFields: List<Place.Field> =
            listOf(Place.Field.NAME, Place.Field.ID, Place.Field.ADDRESS, Place.Field.LAT_LNG)

        // Use the builder to create a FindCurrentPlaceRequest.
        val request: FindCurrentPlaceRequest = FindCurrentPlaceRequest.newInstance(placeFields)

        // Call findCurrentPlace and handle the response (first check that the user has granted permission).
        if (ContextCompat.checkSelfPermission(this.requireContext(), ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this.requireContext(), ACCESS_COARSE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            // Retrieve likely places based on the device's current location
            lifecycleScope.launch {
                try {
                    val response = placesClient.findCurrentPlace(request).await()
//                    binding.responseView.text = response.prettyPrint()

                    val strongestCandidate = response.placeLikelihoods[0].place

                    mMap.addMarker(MarkerOptions().position(strongestCandidate.latLng)
                        .title(strongestCandidate.name)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.add)
                    ))
                    Log.i("googleMap","${strongestCandidate.latLng.latitude}")
                    Log.i("googleMap","${strongestCandidate.latLng.latitude.toString()}")
                    Log.i("googleMap","${strongestCandidate.latLng.longitude}")
                    Log.i("googleMap","${strongestCandidate.latLng.longitude.toString()}")
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(strongestCandidate.latLng))
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(10f),2000, null)
                    // Enable scrolling on the long list of likely places
                    val movementMethod = ScrollingMovementMethod()
//                    binding.responseView.movementMethod = movementMethod
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.i("googleMap","$e")
//                    binding.responseView.text = e.message
                }
            }
        } else {
            Log.d(TAG, "LOCATION permission not granted")
            checkPermissionThenFindCurrentPlace()
        }
    }
}
