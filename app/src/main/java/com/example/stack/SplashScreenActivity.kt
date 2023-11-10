package com.example.stack

import android.animation.Animator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView

//class SplashScreenActivity : AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//
//        // First, you need to get access to the `SplashScreen` instance.
//        // You do that by calling `installSplashScreen()` preferably before
//        //         `super.onCreate()`. On top of creating the instance, it also
//        // set your Application theme to the one set in `postSplashScreenTheme`.
//        val splashScreen = installSplashScreen()
//
//        super.onCreate(savedInstanceState)
//        // Our content view contains the Lottie animation
//        setContentView(R.layout.activity_splash)
//
//        // We set the OnExitAnimationListener to customize our splash screen animation.
//        // This will allow us to take over the splash screen removal animation.
//        splashScreen.setOnExitAnimationListener { vp ->
//            val lottieView = findViewById<LottieAnimationView>(R.id.animationView)
//            lottieView.enableMergePathsForKitKatAndAbove(true)
//
//            // We compute the delay to wait for the end of the splash screen icon
//            // animation.
//            val splashScreenAnimationEndTime =
//                Instant.ofEpochMilli(vp.iconAnimationStartMillis + vp.iconAnimationDurationMillis)
//            val delay = Instant.now(Clock.systemUTC()).until(
//                splashScreenAnimationEndTime,
//                ChronoUnit.MILLIS
//            )
//
//            // Once the delay expires, we start the lottie animation
//            lottieView.postDelayed({
//                vp.view.alpha = 0f
//                vp.iconView.alpha = 0f
//                lottieView!!.playAnimation()
//            }, delay)
//
//            // Finally we dismiss display our app content using a
//            // nice circular reveal
//            lottieView.addAnimatorListener(object : AnimatorListenerAdapter()
//            {
//                override fun onAnimationEnd(animation: Animator) {
////                    val contentView = findViewById<View>(android.R.id.content)
////                    val imageView = findViewById<ImageView>(R.id.imageView)
////
////                    val animator = ViewAnimationUtils.createCircularReveal(
////                        imageView,
////                        contentView.width / 2,
////                        contentView.height / 2,
////                        0f,
////                        max(contentView.width, contentView.height).toFloat()
////                    ).setDuration(600)
////
////                    imageView.visibility = View.VISIBLE
////                    animator.start()
//                    val intent = Intent(this@SplashScreenActivity, MainActivity::class.java)
//                    startActivity(intent)
//                    finish()
////                    overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out) // Revert to default animation
//
//                }
//            })
//        }
//    }
//
//    override fun onResume() {
//        super.onResume()
//        // We display our landing activity edge to edge just like the splash screen
//        // to have a seamless transition from the system splash screen.
//        // This is done in onResume() so we are sure that our Activity is attached
//        // to its window.
//        WindowCompat.setDecorFitsSystemWindows(window, false)
//    }
//
//    override fun onStop() {
//        super.onStop()
//        finish()
//    }
//}

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {
    private val delayMillis: Long = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val lottieView = findViewById<LottieAnimationView>(R.id.animationView)
        fun navigateToMainActivity() {
            val i = Intent(
                this,
                MainActivity::class.java
            )
            startActivity (i)
            overridePendingTransition (android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }



        lottieView.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator) {
            }

            override fun onAnimationCancel(p0: Animator) {
            }

            override fun onAnimationRepeat(p0: Animator) {
            }

            override fun onAnimationEnd(animation: Animator) {

                Log.i("lottie", "onAnimationEnd")
                Handler(Looper.getMainLooper()).postDelayed({
                    navigateToMainActivity()

                }, delayMillis)
            }
        })
    }

}






