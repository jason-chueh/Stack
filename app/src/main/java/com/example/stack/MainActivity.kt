package com.example.stack

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.NavDestination

import androidx.navigation.findNavController
import com.example.stack.data.network.PythonManager
import com.example.stack.databinding.ActivityMainBinding
import com.example.stack.service.ACTION_SHOW_WORKOUT_FRAGMENT
import com.example.stack.login.UserManager
import com.example.stack.util.CurrentFragmentType
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        PythonManager.initialize(this)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupBottomNav()
        setupNavController()

        navigateToWorkoutFragmentWithIntent(intent)

        viewModel.currentFragmentType.observe(
            this,
            Observer {
                Log.i("type","~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~")
                Log.i("type","[${viewModel.currentFragmentType.value}]")
                Log.i("type","~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~")


                if(it == CurrentFragmentType.LOGIN){
                    binding.bottomNavView.visibility = View.GONE
                }
                else{
                    binding.bottomNavView.visibility = View.VISIBLE

                }
            }
        )
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        navigateToWorkoutFragmentWithIntent(intent)
    }

    private fun setupNavController() {
        findNavController(R.id.myNavHostFragment).addOnDestinationChangedListener { navController: NavController, _: NavDestination, _: Bundle? ->
            viewModel.currentFragmentType.value = when (navController.currentDestination?.id) {
                R.id.homeFragment -> CurrentFragmentType.HOME
                R.id.chatroomFragment -> CurrentFragmentType.CHATROOM
                R.id.findBroFragment -> CurrentFragmentType.FINDBRO
                R.id.userFragment -> CurrentFragmentType.USER
                R.id.loginDialog -> CurrentFragmentType.LOGIN
                R.id.planFragment -> CurrentFragmentType.PLAN
                R.id.positionCorrectionFragment-> CurrentFragmentType.POSITION
                else -> viewModel.currentFragmentType.value
            }
        }
    }

    private fun setupBottomNav() {
        binding.bottomNavView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.navigateToHomeFragment())
                    return@setOnItemSelectedListener true
                }

                R.id.navigation_user -> {
                    Log.i("test", "${UserManager.user}")
                    when (viewModel.isLoggedIn) {
                        true -> {
                            findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.navigateToUserFragment())
                            return@setOnItemSelectedListener true
                        }

                        false -> {
                            findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.navigateToLoginDialog())
                            return@setOnItemSelectedListener false
                        }
                    }
                }

                R.id.navigation_find_bro -> {

                    findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.navigateToFindLocationFragment())
                    return@setOnItemSelectedListener true
                }

                R.id.navigation_plan -> {

                    findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.navigateToGenderFragment())
                    return@setOnItemSelectedListener true
                }

                R.id.navigation_position -> {

                    findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.navigateToPositionCorrectionFragment())
                    return@setOnItemSelectedListener true
                }

                else -> {
                    return@setOnItemSelectedListener false
                }

            }
        }
    }
    private fun navigateToWorkoutFragmentWithIntent(intent: Intent?){
        if(intent?.action == ACTION_SHOW_WORKOUT_FRAGMENT){
            findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.navigateToWorkoutFragment())
        }
    }
}