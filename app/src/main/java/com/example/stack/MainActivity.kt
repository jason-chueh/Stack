package com.example.stack

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil

import androidx.navigation.findNavController
import com.example.stack.databinding.ActivityMainBinding
import com.example.stack.login.UserManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupBottomNav()
    }

    private fun setupBottomNav() {
        binding.bottomNavView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.navigateToHomeFragment())
                    return@setOnItemSelectedListener true
                }

                R.id.navigation_user -> {
                    Log.i("test","${UserManager.user.value?.id}")
                    when (viewModel.isLoggedIn) {
                        true -> {
                            findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.navigateToUserFragment())
                            return@setOnItemSelectedListener true
                        }
                    false ->{
                        findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.navigateToLoginDialog())
                        return@setOnItemSelectedListener false
                    }
                }
            }
            R.id.navigation_find_bro -> {

            findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.navigateToFindBroFragment())
            return@setOnItemSelectedListener true
        }
            R.id.navigation_plan -> {

            findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.navigateToPlanFragment())
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
}