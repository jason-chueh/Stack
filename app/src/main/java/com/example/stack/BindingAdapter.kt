package com.example.stack

import android.transition.Transition
import android.util.Log
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.ImageViewTarget


@BindingAdapter("imageUrl")
fun bindImage(imgView: ImageView, imgUrl: String?) {
    imgUrl?.let {
        val imgUri = it.toUri().buildUpon().build()
        Glide.with(imgView.context)
            .load(imgUri)
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.loading)
                    .error(R.drawable.loading)
            )
            .into(imgView)
    }
}


@BindingAdapter("imageResourceFromTarget")
fun setImageResourceFromText(imageView: ImageView, inputText: String?) {
    // Check if the inputText is not null or empty
    if (!inputText.isNullOrBlank()) {
        // Logic to determine the image resource based on the inputText
        val imageResource = getImageResourceForText(inputText)

        // Set the image resource for the ImageView
        imageView.setImageResource(imageResource)
    } else {
        Log.i("exercisePic","nullorblank")
        // If inputText is null or empty, you can set a default image or do nothing
        imageView.setImageResource(R.drawable.core)
    }
}

// Define a function to map input text to image resources (customize this as needed)
private fun getImageResourceForText(inputText: String): Int {
    // Example logic: Map inputText to corresponding image resource IDs
    return when (inputText) {
        "abs"-> R.drawable.core
        "abductors" -> R.drawable.glutes
        "adductors" -> R.drawable.glutes
        "biceps" -> R.drawable.bicep
        "calves" -> R.drawable.calf
        "cardiovascular system" -> R.drawable.quad
        "delts"-> R.drawable.delt
        "forearms" -> R.drawable.forearm
        "glutes" -> R.drawable.glutes
        "hamstrings" -> R.drawable.glutes
        "lats" -> R.drawable.upper_back
        "levator scapulae" -> R.drawable.upper_back
        "pectorals" -> R.drawable.chest
        "quads" -> R.drawable.quad
        "serratus anterior" -> R.drawable.core
        "spine" -> R.drawable.upper_back
        "traps" -> R.drawable.upper_back
        "triceps" -> R.drawable.tricep
        "upper back" -> R.drawable.upper_back
        else -> R.drawable.core
    }
}

