package com.example.stack.findbro

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.stack.NavigationDirections
import com.example.stack.R
import com.example.stack.data.dataclass.User
import com.example.stack.databinding.DialogBroChattingBinding
import com.example.stack.databinding.FragmentMapsBinding
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

private const val RESULT_SEPARATOR = "\n---\n\t"
private const val FIELD_SEPARATOR = "\n\t"

@AndroidEntryPoint
class MapsFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener,
    GoogleMap.OnMyLocationClickListener {
    lateinit var binding: FragmentMapsBinding
    lateinit var mMap: GoogleMap
    private lateinit var placesClient: PlacesClient
    private val viewModel: MapsViewModel by viewModels()

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
        val adapter = BroAdapter(fun(user: User): Unit {
            showDialog(user)
        })

        binding = FragmentMapsBinding.inflate(inflater, container, false)

        binding.broRecycleView.visibility = View.GONE
        binding.broRecycleView.adapter = adapter
        binding.shimmerLayout.startShimmer()
        viewModel.getUserFromFireStore()
        viewModel.allUsersFromFireStore.observe(viewLifecycleOwner){
            Log.i("googleMaps","$it")
        }
        placesClient = Places.createClient(this.requireContext())
//        viewModel.testApi()
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?

        mapFragment?.getMapAsync(this)

        viewModel.sortedUserList.observe(viewLifecycleOwner) {
            Log.i("Jason", "sortedUserList changed!")
            Log.i("Jason", "$it")

            for (user in it) {
                try {
                    val latitude = user.gymLatitude?.toDouble()
                    val longitude = user.gymLongitude?.toDouble()

                    val latLng = LatLng(latitude!!, longitude!!)
                    mMap.addMarker(
                        MarkerOptions().position(latLng).title(user.name)
                    )

                    // Now, you have latLng as a LatLng object
                } catch (e: Exception) {
                    Log.i("Jason", "$e")
                }
            }
        }
        viewModel.currentLatLng.observe(viewLifecycleOwner) {
            Log.i("Jason", "currentLatLng change")
            Log.i("Jason", "$it")
        }
        viewModel.mediatorLiveData.observe(viewLifecycleOwner) {
            Log.i("Jason", "mediatorLiveData change")
            Log.i("Jason", "$it")

            viewModel.calculateDistanceAndSort()
        }
        viewModel.sortedUserList.observe(viewLifecycleOwner) {
            Log.i("Jason", "$it")


            binding.broRecycleView.visibility = View.VISIBLE
            binding.shimmerLayout.stopShimmer()
            binding.shimmerLayout.visibility = View.INVISIBLE


            adapter.submitList(it)
        }

        viewModel.chatroomToGo.observe(viewLifecycleOwner) {
            val bottomNavigation =
                requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavView)
            bottomNavigation.selectedItemId = R.id.navigation_chatroom
            it?.let { findNavController().navigate(NavigationDirections.navigateToChatFragment(it)) }

        }

        return binding.root
    }

    private fun showDialog(user: User) {
        val dialog = Dialog(this.requireContext())
        val dialogBinding: DialogBroChattingBinding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.dialog_bro_chatting,
            null,
            false
        )
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(dialogBinding.root)

        dialog.show()
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogBinding.lifecycleOwner = viewLifecycleOwner

        dialogBinding.root.setOnClickListener {
            dialog.dismiss()
        }
        dialogBinding.dismissButton.setOnClickListener {
            dialog.dismiss()
        }
        dialogBinding.chatButton.setOnClickListener {
            viewModel.createChatroom(user)
//            val bottomNavigation = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavView)
//            bottomNavigation.selectedItemId = R.id.navigation_position
//            chatroom?.let{findNavController().navigate(NavigationDirections.navigateToChatFragment(it))}
            dialog.dismiss()
        }
        dialogBinding.user = user
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
//        Log.i("googleMap","The map is ready!")
        mMap = googleMap

        checkPermissionThenFindCurrentPlace()
//        mMap.isMyLocationEnabled = true
//        mMap.setOnMyLocationButtonClickListener(this)
//        mMap.setOnMyLocationClickListener(this)
    }

    override fun onMyLocationClick(location: Location) {
        Toast.makeText(this.requireContext(), "Current location:\n$location", Toast.LENGTH_LONG)
            .show()
    }

    override fun onMyLocationButtonClick(): Boolean {
        Toast.makeText(this.requireContext(), "MyLocation button clicked", Toast.LENGTH_SHORT)
            .show()
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false
    }

    private fun checkPermissionThenFindCurrentPlace() {
        when {
            (ContextCompat.checkSelfPermission(
                this.requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this.requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED) -> {
                // You can use the API that requires the permission.
                findCurrentPlace()
            }

            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)
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
                    grantResult == PackageManager.PERMISSION_GRANTED && (permission == Manifest.permission.ACCESS_FINE_LOCATION || permission == Manifest.permission.ACCESS_COARSE_LOCATION)
                } != null
        )
        // At least one location permission has been granted, so proceed with Find Current Place
            findCurrentPlace()
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
    private fun findCurrentPlace() {
        // Use fields to define the data types to return.
        val placeFields: List<Place.Field> =
            listOf(Place.Field.NAME, Place.Field.ID, Place.Field.ADDRESS, Place.Field.LAT_LNG)

        // Use the builder to create a FindCurrentPlaceRequest.
        val request: FindCurrentPlaceRequest = FindCurrentPlaceRequest.newInstance(placeFields)

        // Call findCurrentPlace and handle the response (first check that the user has granted permission).
        if (ContextCompat.checkSelfPermission(
                this.requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) ==
            PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                this.requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            viewModel.findCurrentPlace(placesClient, request, mMap)

        } else {
            Log.d(TAG, "LOCATION permission not granted")
            Log.i("Jason", "called!")
            checkPermissionThenFindCurrentPlace()

        }
    }
}