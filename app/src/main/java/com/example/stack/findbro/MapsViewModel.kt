package com.example.stack.findbro

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stack.BuildConfig
import com.example.stack.R
import com.example.stack.data.StackRepository
import com.example.stack.data.dataclass.DistanceMatrixResponse
import com.example.stack.data.dataclass.User
import com.google.android.gms.common.api.Api
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MapsViewModel @Inject constructor(private val stackRepository: StackRepository) :
    ViewModel() {

    var allUsers: LiveData<List<User>> = stackRepository.getUsers()

    var currentLatLng = MutableLiveData<String>()

    val mediatorLiveData = allUsers.combineWith(currentLatLng) { allUsers, currentLatLng ->
        allUsers
    }

    var _sortedUserList = MutableLiveData<List<User>>()

    val sortedUserList: LiveData<List<User>>
        get() = _sortedUserList

    init{
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                users.forEach {
                    Log.i("db","user")
                    stackRepository.upsertUser(it)
                }
//                stackRepository.upsertUser()
            }
        }
    }
    @SuppressLint("MissingPermission")
    fun findCurrentPlace(
        placesClient: PlacesClient,
        request: FindCurrentPlaceRequest,
        mMap: GoogleMap
    ) {
        viewModelScope.launch {
            try {
                val response = placesClient.findCurrentPlace(request).await()
//                    binding.responseView.text = response.prettyPrint()

                val strongestCandidate = response.placeLikelihoods[0].place

                mMap.addMarker(
                    MarkerOptions().position(strongestCandidate.latLng)
                        .title(strongestCandidate.name)
                        .icon(
                            BitmapDescriptorFactory.fromResource(R.drawable.add)
                        )
                )

                mMap.moveCamera(CameraUpdateFactory.newLatLng(strongestCandidate.latLng))
                mMap.animateCamera(CameraUpdateFactory.zoomTo(20f), 2000, null)
                currentLatLng.value =
                    strongestCandidate.latLng.latitude.toString() + "," + strongestCandidate.latLng.longitude.toString()
            } catch (e: Exception) {
                e.printStackTrace()
                Log.i("googleMap", "$e")
//                    binding.responseView.text = e.message
            }
        }
    }


    fun calculateDistanceAndSort() {
//        viewModelScope.launch {
//            withContext(Dispatchers.IO){
//                stackRepository.upsertUser(User(
//                    id = "1",
//                    name = "John Doe",
//                    email = "johndoe@example.com",
//                    picture = "https://media.wired.com/photos/598e35fb99d76447c4eb1f28/master/pass/phonepicutres-TA.jpg",
//                    gymLatitude = "25.038658708007013",
//                    gymLongitude = "121.5767419197247437.7749"
//                ))
//            }
//            Log.i("googleMap","${allUsers.value}")
//        }
        viewModelScope.launch {
            if (allUsers.value?.isNotEmpty() == true) {

                val resultList = mutableListOf<User>()
                withContext(Dispatchers.IO) {
                    val filteredUser =
                        allUsers.value?.filter { it.gymLatitude != null && it.gymLongitude != null }
                    val destinations = filteredUser
                        ?.mapNotNull { it.gymLatitude?.let { lat -> it.gymLongitude?.let { lon -> "$lat,$lon" } } }
                        ?.joinToString(separator = "|")
//            val origin = users.map { it.gymLatitude + "," + it.gymLongitude }.joinToString { "|" }
                    Log.i("googleMap", "$destinations")

                    val distanceMatrixList = destinations?.let {
                        stackRepository.getDistanceMatrix(
                            currentLatLng.value!!,
                            it,
                            BuildConfig.MAPS_API_KEY
                        )
                    }
                    distanceMatrixList?.rows?.get(0)?.elements?.mapIndexed { index, item ->
                        ElementWithIndex(
                            index,
                            item.distance,
                            item.duration
                        )
                    }?.sortedBy { it.duration.value }

                    if (filteredUser != null) {
                        for (i in filteredUser) {
                            resultList.add(i)
                            //                    stackRepository.upsertUser(i)
                        }
                    }

                    Log.i("googleMap", "$resultList")
                }

                _sortedUserList.postValue(resultList)

            }
//            stackRepository.getDistanceMatrix()
        }
    }
}

//fun <T, K, R> LiveData<T>.combineWith(
//    liveData: LiveData<K>,
//    block: (T?, K?) -> R
//): LiveData<R> {
//    val result = MediatorLiveData<R>()
//    result.addSource(this) {
//        result.value = block(this.value, liveData.value)
//    }
//    result.addSource(liveData) {
//        result.value = block(this.value, liveData.value)
//    }
//    return result
//}
fun <T, K, R> LiveData<T>.combineWith(
    liveData: LiveData<K>,
    block: (T?, K?) -> R
): LiveData<R> {
    val result = MediatorLiveData<R>()
    var source1Value: T? = null
    var source2Value: K? = null

    val updateResult = {
        result.value = block(source1Value, source2Value)
    }

    result.addSource(this) { value ->
        source1Value = value
        if (source1Value != null && source2Value != null) {
            updateResult()
        }
    }

    result.addSource(liveData) { value ->
        source2Value = value
        if (source1Value != null && source2Value != null) {
            updateResult()
        }
    }

    return result
}

data class ElementWithIndex(
    val index: Int,
    val distance: DistanceMatrixResponse.TextValue,
    val duration: DistanceMatrixResponse.TextValue
)

//mock data
val users = listOf(
    User(
        id = "1",
        name = "John Doe",
        email = "johndoe@example.com",
        picture = "https://media.wired.com/photos/598e35fb99d76447c4eb1f28/master/pass/phonepicutres-TA.jpg",
        gymLatitude = "25.038658708007013",
        gymLongitude = "121.5767419197247437.7749"
    ),
    User(
        id = "2",
        name = "Jane Smith",
        email = "janesmith@example.com",
        picture = "https://media.sproutsocial.com/uploads/2022/06/profile-picture.jpeg",
        gymLatitude = "34.0522",
        gymLongitude = "-118.2437"
    ),
    User(
        id = "3",
        name = "Alice Johnson",
        email = "alicejohnson@example.com",
        picture = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT0D1BTf79SsmH4qhA0b7fDNqAOEr1SpHMBnw&usqp=CAU",
        gymLatitude = "40.7128",
        gymLongitude = "-74.0060"
    ),
    User(
        id = "4",
        name = "Bob Anderson",
        email = "bobanderson@example.com",
        picture = "https://wallpapers.com/images/hd/cool-profile-picture-ld8f4n1qemczkrig.jpg",
        gymLatitude = "41.8781",
        gymLongitude = "-87.6298"
    )
)