package com.example.stack.home.workout

import android.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.stack.data.FakeStackRepository
import com.example.stack.data.dataclass.RepsAndWeights
import com.example.stack.data.dataclass.TemplateExerciseRecord
import com.example.stack.data.dataclass.User
import com.example.stack.data.dataclass.toTemplateExerciseWithCheck
import com.example.stack.getOrAwaitValue
import com.example.stack.login.UserManager
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
@RunWith(AndroidJUnit4::class)
class WorkoutViewModelTest{
    private lateinit var viewModel: WorkoutViewModel

    @get: Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var userManager: UserManager

    @Before
    fun setUp(){
        userManager = mock(UserManager::class.java)
        MockitoAnnotations.initMocks(this)
        `when`(userManager.user).then {
            User("test123","testUser","")
        }

//        userManager.updateUser(User("test123","testUser",""))

//        userManager.user = User("test123","testUser","")
        viewModel = WorkoutViewModel(FakeStackRepository(), userManager ,"123")
    }

    @Test
    fun setDataListFromBundle_normalSituation_noReturn(){
        val templateExerciseRecord1 = TemplateExerciseRecord(
            templateId = "template1",
            exerciseName = "Push-ups",
            exerciseId = "exercise1",
            repsAndWeights = mutableListOf(
                RepsAndWeights(reps = 10, weight = 0),
                RepsAndWeights(reps = 12, weight = 0),
                RepsAndWeights(reps = 15, weight = 0)
            )
        )

        val templateExerciseRecord2 = TemplateExerciseRecord(
            templateId = "template2",
            exerciseName = "Squats",
            exerciseId = "exercise2",
            repsAndWeights = mutableListOf(
                RepsAndWeights(reps = 8, weight = 50),
                RepsAndWeights(reps = 10, weight = 60)
            )
        )

        val templateExerciseRecord3 = TemplateExerciseRecord(
            templateId = "template3",
            exerciseName = "Pull-ups",
            exerciseId = "exercise3",
            repsAndWeights = mutableListOf(
                RepsAndWeights(reps = 5, weight = 0),
                RepsAndWeights(reps = 6, weight = 0),
                RepsAndWeights(reps = 7, weight = 0)
            )
        )

        val templateExerciseList = listOf(templateExerciseRecord1, templateExerciseRecord2, templateExerciseRecord3)
        val expectedList = templateExerciseList.map{ userManager.user?.let { it1 -> it.toTemplateExerciseWithCheck(userId = it1.id, startTime = System.currentTimeMillis()) } }

        viewModel.setDataListFromBundle(templateExerciseList)

        val value = viewModel.dataList.getOrAwaitValue()

        assertEquals(value.size, expectedList.size)
    }

    @Test
    fun calculateScroll_positionFirst_noReturn(){


    }
}