package com.example.stack

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import com.example.stack.databinding.ActivityMainBinding
import com.example.stack.login.UserManager
import com.example.stack.service.ACTION_SHOW_WORKOUT_FRAGMENT
import com.example.stack.util.CurrentFragmentType
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var userManager: UserManager

    private val viewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = ContextCompat.getColor(this, R.color.primaryColor)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupBottomNav()
        setupNavController()



        navigateToWorkoutFragmentWithIntent(intent)

        viewModel.currentFragmentType.observe(
            this
        ) {
            Log.i("type", "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~")
            Log.i("type", "[${viewModel.currentFragmentType.value}]")
            Log.i("type", "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~")


            if (it == CurrentFragmentType.LOGIN) {
                binding.bottomNavView.visibility = View.GONE
            } else {
                binding.bottomNavView.visibility = View.VISIBLE

            }
        }
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
                R.id.positionCorrectionFragment -> CurrentFragmentType.POSITION
                else -> viewModel.currentFragmentType.value
            }
        }
    }

    private fun setupBottomNav() {
        binding.bottomNavView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    when (viewModel.isLoggedIn) {
                        true -> {
                            if (userManager.isTraining) {
                                findNavController(R.id.myNavHostFragment).navigate(
                                    NavigationDirections.navigateToWorkoutFragment(
                                        null
                                    )
                                )
                                return@setOnItemSelectedListener true
                            }
                            findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.navigateToHomeFragment())
                            return@setOnItemSelectedListener true
                        }
                        false -> {
                            findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.navigateToLoginDialog())
                            return@setOnItemSelectedListener false
                        }
                    }
                }

                R.id.navigation_user -> {
                    Log.i("test", "${userManager.user}")
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
                    when (viewModel.isLoggedIn) {
                        true -> {
                            if (userManager.user?.gymLongitude != null) {
                                findNavController(R.id.myNavHostFragment).navigate(
                                    NavigationDirections.navigateToMapsFragment()
                                )
                            } else {
                                findNavController(R.id.myNavHostFragment).navigate(
                                    NavigationDirections.navigateToFindLocationFragment()
                                )
                            }
                            return@setOnItemSelectedListener true
                        }

                        false -> {
                            findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.navigateToLoginDialog())
                            return@setOnItemSelectedListener false
                        }
                    }
                }

                R.id.navigation_chatroom -> {
                    when (viewModel.isLoggedIn) {
                        true -> {
                            findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.navigateToChatroomFragment())
                            return@setOnItemSelectedListener true
                        }

                        false -> {
                            findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.navigateToLoginDialog())
                            return@setOnItemSelectedListener false
                        }
                    }
                }

                else -> {
                    return@setOnItemSelectedListener false
                }
            }
        }
    }

    private fun navigateToWorkoutFragmentWithIntent(intent: Intent?) {
        if (intent?.action == ACTION_SHOW_WORKOUT_FRAGMENT) {
            findNavController(R.id.myNavHostFragment).navigate(
                NavigationDirections.navigateToWorkoutFragment(
                    null
                )
            )
        }
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

}