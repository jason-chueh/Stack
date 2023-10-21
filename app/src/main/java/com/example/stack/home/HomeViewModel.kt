package com.example.stack.home

import android.animation.ValueAnimator
import android.util.Log
import android.view.View
import android.view.animation.BounceInterpolator
import androidx.core.net.toUri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stack.data.StackRepository
import com.example.stack.data.dataclass.Chatroom
import com.example.stack.data.dataclass.ExerciseRecord
import com.example.stack.data.dataclass.RepsAndWeights
import com.example.stack.data.dataclass.Template
import com.example.stack.data.dataclass.TemplateExerciseRecord
import com.example.stack.data.dataclass.Workout
import com.example.stack.login.UserManager
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.UUID

import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val stackRepository: StackRepository) :
    ViewModel() {

    val userExerciseRecords = MutableLiveData<List<ExerciseRecord>?>()
    val userWorkoutRecords = MutableLiveData<List<Workout>?>()

    fun getTotalWeight(): Int? {
        userExerciseRecords.value?.let { exerciseRecords ->
            return exerciseRecords.flatMap { it.repsAndWeights }
                .sumBy { it.reps * it.weight }
        }
        return null
    }

    fun animateIcon(view: View) {
        val bounceAnimator = ValueAnimator.ofFloat(1f, 20f, 10f, 1f).apply {
            duration = 20000
            interpolator = BounceInterpolator()
            addUpdateListener {
                val scaleValue = it.animatedValue as Float
                view.scaleX = scaleValue
                view.scaleY = scaleValue
            }
        }
        bounceAnimator.start()
    }


    fun uploadImageToFireStorage(stringOfUri: String) {
        var currentUri = ""
        val reference = FirebaseStorage.getInstance().reference
        val path = UUID.randomUUID().leastSignificantBits
        val imageRef = reference.child("images/$path.jpg")

        val uploadTask = imageRef.putFile(stringOfUri.toUri())

        uploadTask.addOnSuccessListener { uri ->
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                currentUri = uri.toString()
                UserManager.updateUser(UserManager.user?.copy(picture = currentUri))
                UserManager.user?.let { stackRepository.uploadUserToFireStore(it) }
                viewModelScope.launch {
                    UserManager.user?.let { stackRepository.upsertUser(it) }
                    UserManager.user?.let{ stackRepository.uploadUserToFireStore(it)}
                }
//                uploadProfileToFirebase(currentUri)
                Log.i("personal image", "upload successfully, url is $uri")
            }
        }.addOnFailureListener {
            Log.e("personal image", "failed: $it")
        }
        Log.i("personal image", "currentUri is $currentUri")
    }

    fun getUserExerciseData() {

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val exerciseRecordList =
                    UserManager.user?.id?.let { stackRepository.getAllExercisesByUserId(it) }

                userExerciseRecords.postValue(exerciseRecordList)
            }
        }
    }

    fun getUserWorkoutData() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val workoutList =
                    UserManager.user?.id?.let { stackRepository.findAllWorkoutById(it) }
                userWorkoutRecords.postValue(workoutList)
            }
        }
    }

    fun deleteAllTemplate() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                stackRepository.deleteAllTemplate()
            }
        }
    }

    fun exerciseApi() {
        viewModelScope.launch {
            stackRepository.refreshExerciseDb()
        }
    }

    fun exerciseApiToDatabase(){
        viewModelScope.launch {
            stackRepository.exerciseApiToDatabase()
        }
    }

    fun exerciseApiRe() {
        viewModelScope.launch {
            stackRepository.exerciseApiToFireStore()
        }
    }

    fun upsertTemplate() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                stackRepository.upsertTemplate(personalTemplate)
                stackRepository.upsertTemplate(legTemplate)
            }
        }
    }

    fun searchTemplateByUserId() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                var resultList =
                    UserManager.user?.id?.let { stackRepository.searchTemplateIdListByUserId(it) }
                Log.i("template", "$resultList")
            }
        }
    }

    fun upsertTemplateExerciseRecord() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                stackRepository.upsertTemplateExerciseRecord(templateExerciseRecord1)
            }
        }
    }

    fun upsertTemplateExerciseRecordList() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                stackRepository.upsertTemplateExerciseRecord(
                    listOf(
                        templateExerciseRecord1,
                        templateExerciseRecord2,
                        templateExerciseRecord3,
                        templateExerciseRecord4,
                        templateExerciseRecord5,
                        templateExerciseRecord6,
                        templateExerciseRecord7,
                        templateExerciseRecord8
                    )
                )
            }
        }
    }

    fun searchAllTemplateAndExerciseByUserId() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val resultList =
                    UserManager.user?.let { stackRepository.searchTemplateIdListByUserId(it.id) }
                if (resultList != null) {
                    for (i in resultList) {
                        var exerciserList =
                            stackRepository.getTemplateExerciseRecordListByTemplateId(i)
                        Log.i("template", "$exerciserList")
                    }
                }
            }
        }
    }

//    fun createChatroom() {
//        viewModelScope.launch {
//            if (UserManager.user?.id != null) {
//                stackRepository.createChatroomAtFireStore(
//                    Chatroom(
//                        userId1 = UserManager.user!!.id,
//                        userId2 = "23426",
//                        userName = listOf("ww", "232"),
//                        userPic = listOf("", ""),
//                        lastMessage = "hi",
//                        lastMessageTime = Calendar.getInstance().timeInMillis
//                    )
//                )
//            }
//        }
//    }

    fun deleteYoutube() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                stackRepository.deleteYoutubeById("tlfahNdNPPI")
            }
        }
    }

    val legTemplate = Template(
        templateId = "2",
        userId = "K8O0QzYjHrRkGTyJz1rgXpyaggn2",
        templateName = "Killer Leg Workout Template",
    )

    val personalTemplate = Template(
        templateId = "1",
        userId = "K8O0QzYjHrRkGTyJz1rgXpyaggn2",
        templateName = "Full Body Workout Template",
    )
    val templateExerciseRecord1 = TemplateExerciseRecord(
        templateId = "1",
        exerciseName = "barbell bench front squat",
        exerciseId = "0024",
        repsAndWeights = mutableListOf<RepsAndWeights>(
            RepsAndWeights(10, 20),
            RepsAndWeights(10, 40),
            RepsAndWeights(10, 60)
        )
    )
    val templateExerciseRecord2 = TemplateExerciseRecord(
        templateId = "1",
        exerciseName = "barbell bench press",
        exerciseId = "0025",
        repsAndWeights = mutableListOf<RepsAndWeights>(
            RepsAndWeights(12, 20),
            RepsAndWeights(12, 40),
            RepsAndWeights(12, 60)
        )
    )
    val templateExerciseRecord3 = TemplateExerciseRecord(
        templateId = "1",
        exerciseName = "barbell bench squat",
        exerciseId = "0026",
        repsAndWeights = mutableListOf<RepsAndWeights>(
            RepsAndWeights(12, 40),
            RepsAndWeights(12, 60)
        )
    )
    val templateExerciseRecord4 = TemplateExerciseRecord(
        templateId = "1",
        exerciseName = "barbell biceps curl",
        exerciseId = "2407",
        repsAndWeights = mutableListOf<RepsAndWeights>()
    )

    val templateExerciseRecord5 = TemplateExerciseRecord(
        templateId = "2",
        exerciseName = "barbell bench front squat",
        exerciseId = "0024",
        repsAndWeights = mutableListOf<RepsAndWeights>()
    )
    val templateExerciseRecord6 = TemplateExerciseRecord(
        templateId = "2",
        exerciseName = "barbell deadlift",
        exerciseId = "0032",
        repsAndWeights = mutableListOf<RepsAndWeights>()
    )
    val templateExerciseRecord7 = TemplateExerciseRecord(
        templateId = "2",
        exerciseName = "barbell clean and press",
        exerciseId = "0028",
        repsAndWeights = mutableListOf<RepsAndWeights>()
    )
    val templateExerciseRecord8 = TemplateExerciseRecord(
        templateId = "2",
        exerciseName = "dumbbell bench squat",
        exerciseId = "0291",
        repsAndWeights = mutableListOf<RepsAndWeights>()
    )


}

