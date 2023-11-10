package com.example.stack.home

import android.animation.ValueAnimator
import android.util.Log
import android.view.View
import android.view.animation.BounceInterpolator
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stack.data.StackRepository
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
import java.util.UUID
import java.util.concurrent.TimeUnit

import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val stackRepository: StackRepository,
    val userManager: UserManager
) :
    ViewModel() {
    val userExerciseRecords: LiveData<List<ExerciseRecord>?>
        get() = _userExerciseRecords

    private val _userExerciseRecords = MutableLiveData<List<ExerciseRecord>?>()

    val userWorkoutRecords: LiveData<List<Workout>?>
        get() = _userWorkoutRecords

    private val _userWorkoutRecords = MutableLiveData<List<Workout>?>()

    fun getTotalWeight(): Int? {
        _userExerciseRecords.value?.let { exerciseRecords ->
            return exerciseRecords.flatMap { it.repsAndWeights }
                .sumOf { it.reps * it.weight }
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
                userManager.updateUser(userManager.user?.copy(picture = currentUri))

                viewModelScope.launch(Dispatchers.IO) {
                    userManager.user?.let {
                        stackRepository.uploadUserToFireStore(it)
                        stackRepository.upsertUser(it) }
                }
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
                    userManager.user?.id?.let { stackRepository.getAllExercisesByUserId(it) }

                _userExerciseRecords.postValue(exerciseRecordList)
            }
        }
    }

    fun getUserWorkoutData() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val workoutList =
                    userManager.user?.id?.let { stackRepository.findAllWorkoutById(it) }
                _userWorkoutRecords.postValue(workoutList)
            }
        }
    }

    fun exerciseApiToDatabase() {
        viewModelScope.launch {
            stackRepository.exerciseApiToDatabase()
        }
    }


    fun upsertTemplate() {
        viewModelScope.launch(Dispatchers.IO) {

            userManager.user?.id?.let {
                val legTemplate = Template(
                    templateId = "2",
                    userId = it,
                    templateName = "Killer Leg Workout Template",
                )
                val personalTemplate = Template(
                    templateId = "1",
                    userId = it,
                    templateName = "Full Body Workout Template",
                )
                stackRepository.upsertTemplate(personalTemplate)
                stackRepository.upsertTemplate(legTemplate)
            }
        }
    }

    fun upsertTemplateExerciseRecordList() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                stackRepository.upsertTemplateExerciseRecords(
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
    fun getTimeAgo(timestamp: Long): String {
        val currentTimeMillis = System.currentTimeMillis()
        val timeDifferenceMillis = currentTimeMillis - timestamp
        val minutes = TimeUnit.MILLISECONDS.toMinutes(timeDifferenceMillis)
        val hours = TimeUnit.MILLISECONDS.toHours(timeDifferenceMillis)
        val days = TimeUnit.MILLISECONDS.toDays(timeDifferenceMillis)
        val months = days / 30
        val years = days / 365
        return when {
            years > 1 -> "$years years"
            months > 1 -> "$months months"
            days > 1 -> "$days days"
            hours > 1 -> "$hours hours"
            minutes > 1 -> "$minutes minutes"
            else -> "seconds"
        }
    }


    private val templateExerciseRecord1 = TemplateExerciseRecord(
        templateId = "1",
        exerciseName = "barbell bench front squat",
        exerciseId = "0024",
        repsAndWeights = mutableListOf(
            RepsAndWeights(10, 20),
            RepsAndWeights(10, 40),
            RepsAndWeights(10, 60)
        )
    )
    private val templateExerciseRecord2 = TemplateExerciseRecord(
        templateId = "1",
        exerciseName = "barbell bench press",
        exerciseId = "0025",
        repsAndWeights = mutableListOf(
            RepsAndWeights(12, 20),
            RepsAndWeights(12, 40),
            RepsAndWeights(12, 60)
        )
    )
    private val templateExerciseRecord3 = TemplateExerciseRecord(
        templateId = "1",
        exerciseName = "barbell bench squat",
        exerciseId = "0026",
        repsAndWeights = mutableListOf(
            RepsAndWeights(12, 40),
            RepsAndWeights(12, 60)
        )
    )
    private val templateExerciseRecord4 = TemplateExerciseRecord(
        templateId = "1",
        exerciseName = "barbell biceps curl",
        exerciseId = "2407",
        repsAndWeights = mutableListOf()
    )

    private val templateExerciseRecord5 = TemplateExerciseRecord(
        templateId = "2",
        exerciseName = "barbell bench front squat",
        exerciseId = "0024",
        repsAndWeights = mutableListOf()
    )
    private val templateExerciseRecord6 = TemplateExerciseRecord(
        templateId = "2",
        exerciseName = "barbell deadlift",
        exerciseId = "0032",
        repsAndWeights = mutableListOf()
    )
    private val templateExerciseRecord7 = TemplateExerciseRecord(
        templateId = "2",
        exerciseName = "barbell clean and press",
        exerciseId = "0028",
        repsAndWeights = mutableListOf()
    )
    private val templateExerciseRecord8 = TemplateExerciseRecord(
        templateId = "2",
        exerciseName = "dumbbell bench squat",
        exerciseId = "0291",
        repsAndWeights = mutableListOf()
    )
}

