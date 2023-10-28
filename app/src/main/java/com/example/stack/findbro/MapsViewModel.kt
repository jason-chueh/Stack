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
import com.example.stack.data.dataclass.Chatroom
import com.example.stack.data.dataclass.DistanceMatrixResponse
import com.example.stack.data.dataclass.User
import com.example.stack.login.UserManager
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
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class MapsViewModel @Inject constructor(
    private val stackRepository: StackRepository,
    private val userManager: UserManager) :
    ViewModel() {


    private var _allUsersFromFireStore = MutableLiveData<List<User>>()
    val allUsersFromFireStore: LiveData<List<User>>
        get() = _allUsersFromFireStore

    var currentLatLng = MutableLiveData<String>()

    val mediatorLiveData = allUsersFromFireStore.combineWith(currentLatLng) { allUsers, _ ->
        allUsers
    }
    private var _sortedUserList = MutableLiveData<List<User>>()

    val sortedUserList: LiveData<List<User>>
        get() = _sortedUserList

    var chatroomToGo = MutableLiveData<Chatroom>()

    init {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                users.forEach {
                    Log.i("db", "user")
                    stackRepository.upsertUser(it)
                }
            }
        }
    }
    fun getUserFromFireStore(){
        Log.i("googleMaps","called out")
                stackRepository.getUsersFromFireStore {
                    Log.i("googleMaps","called")
                    _allUsersFromFireStore.value = it.toList()
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
                val strongestCandidate = response.placeLikelihoods[0].place

                mMap.addMarker(
                    MarkerOptions().position(strongestCandidate.latLng)
                        .title(strongestCandidate.name)
                        .icon(
                            BitmapDescriptorFactory.fromResource(R.drawable.add)
                        )
                )

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(strongestCandidate.latLng, 15f), 2000, null)
//                mMap.animateCamera(CameraUpdateFactory.zoomTo(11f), 1000, null)

                currentLatLng.value =
                    strongestCandidate.latLng.latitude.toString() + "," + strongestCandidate.latLng.longitude.toString()
            } catch (e: Exception) {
                e.printStackTrace()
                Log.i("googleMap", "$e")
            }
        }
    }


    fun calculateDistanceAndSort() {
        viewModelScope.launch {
            if (allUsersFromFireStore.value?.isNotEmpty() == true) {

                val resultList = mutableListOf<User>()
                withContext(Dispatchers.IO) {
                    val filteredUser =
                        allUsersFromFireStore.value?.filter { it.gymLatitude != null && it.gymLongitude != null && !it.picture.isNullOrBlank() && it.id != userManager.user?.id}
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
                    Log.i("googleMap", "$distanceMatrixList")

                    val result2 =
                        distanceMatrixList?.rows?.get(0)?.elements?.mapIndexed { index, item ->
                            ElementWithIndex(
                                index,
//                            item.distance,
                                item.duration
                            )
                        }?.sortedBy { it.duration?.value }
                    Log.i("google map", "result2 $result2")
                    Log.i("google map", "filteredUser: $filteredUser")
                    if (filteredUser != null && result2 != null) {
                        for (i in result2) {
                            resultList.add(filteredUser[i.index])
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

    fun createChatroom(user: User) {
        if (userManager.user != null) {
            val chatroom = Chatroom(
                userId1 = user.id,
                userId2 = userManager.user!!.id,
                userName = listOf(user.name, userManager.user!!.name),
                userPic = listOf(user.picture, userManager.user!!.picture),
                lastMessageTime = Calendar.getInstance().timeInMillis
            )
            stackRepository.createChatroomAtFireStore(
                chatroom, chatroomCallBack
            )
        }
    }

    private val chatroomCallBack: (chatroom: Chatroom) -> Unit = { chatroom ->
        chatroomToGo.value = chatroom
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
//    val distance: DistanceMatrixResponse.TextValue,
    val duration: DistanceMatrixResponse.TextValue?
)

//mock data
val users = listOf(
    User(
        id = "1",
        name = "John Doe",
        email = "johndoe@example.com",
        picture = "https://media.wired.com/photos/598e35fb99d76447c4eb1f28/master/pass/phonepicutres-TA.jpg",
        gymLatitude = "25.038658708007013",
        gymLongitude = "121.5767419197247437"
    ),
    User(//
        id = "2",
        name = "Jane Smith",
        email = "janesmith@example.com",
        picture = "https://media.sproutsocial.com/uploads/2022/06/profile-picture.jpeg",
        gymLatitude = "25.036849942931823",
        gymLongitude = "121.5678825086946"
    ),
    User(
        id = "3",
        name = "Alice Johnson",
        email = "alicejohnson@example.com",
        picture = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT0D1BTf79SsmH4qhA0b7fDNqAOEr1SpHMBnw&usqp=CAU",
        gymLatitude = "25.035505661816206",
        gymLongitude = "121.56710488737288"
    ),
    User(
        id = "4",
        name = "Bob Anderson",
        email = "bobanderson@example.com",
        picture = "https://wallpapers.com/images/hd/cool-profile-picture-ld8f4n1qemczkrig.jpg",
        gymLatitude = "25.035677968823585",
        gymLongitude = "121.56719318469423"
    )
)