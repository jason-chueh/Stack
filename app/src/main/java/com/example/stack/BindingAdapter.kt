package com.example.stack

import android.annotation.SuppressLint
import android.transition.Transition
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.ImageViewTarget
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import java.lang.Math.abs
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


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

@BindingAdapter("imageUrl")
fun loadImageUrl(view: ImageView, imageUrl: String?) {
    imageUrl?.let {
        Glide.with(view.context)
            .load(imageUrl)
            .into(view)
    }
}

@BindingAdapter("instructionList")
fun setInstructionList(textView: TextView, instructionList: List<String>) {
    var instruction = ""
    instructionList.forEachIndexed { index, s ->  instruction += (index+1).toString()
        instruction += ":  "
        instruction += s
        instruction += "\n"
    }

    textView.text = instruction
}


@SuppressLint("ResourceAsColor")
@BindingAdapter("chips", "mainChip")
fun setChips(chipGroup: ChipGroup, chipTextList: List<String>?, mainChip: String) {
    chipGroup.removeAllViews() // Clear existing chips
    chipGroup.setChipSpacingHorizontalResource(R.dimen.dimen_16)

    val chipList = mutableListOf<String>()
    chipList.add(mainChip)
    if (chipTextList != null) {
        chipList.addAll(chipTextList)
    }

    val textColor = ContextCompat.getColor(chipGroup.context, R.color.white)
    val font = ResourcesCompat.getFont(chipGroup.context, R.font.rubik_medium)
    chipList.forEach { chipText ->
        val chip = Chip(chipGroup.context)
        chip.setChipBackgroundColorResource(R.color.lightBlue)
        chip.setTextColor(textColor)
        chip.typeface = font
        chip.text = chipText
        chip.isClickable = false
        chip.isCheckedIconVisible = false
        chip.isCheckable = false // Adjust this based on your requirements
        chipGroup.addView(chip)
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
        imageView.setImageResource(R.drawable.core_detail)
    }
}

@BindingAdapter("chatroomTime")
fun showChatroomMessageTime(textView: TextView, inputTime: Long){
    val formattedTime = formatMessageTimestamp(inputTime)
    textView.text = formattedTime
}


// Define a function to map input text to image resources (customize this as needed)
private fun getImageResourceForText(inputText: String): Int {
    // Example logic: Map inputText to corresponding image resource IDs
    return when (inputText) {
        "abs"-> R.drawable.core_detail
        "abductors" -> R.drawable.glutes_detail
        "adductors" -> R.drawable.glutes_detail
        "biceps" -> R.drawable.bicep_detail
        "calves" -> R.drawable.calf_detail
        "cardiovascular system" -> R.drawable.quad_detail
        "delts"-> R.drawable.delt_detail
        "forearms" -> R.drawable.forearm_detail
        "glutes" -> R.drawable.glutes_detail
        "hamstrings" -> R.drawable.glutes_detail
        "lats" -> R.drawable.back_detail
        "levator scapulae" -> R.drawable.back_detail
        "pectorals" -> R.drawable.chest_detail
        "quads" -> R.drawable.quad_detail
        "serratus anterior" -> R.drawable.core_detail
        "spine" -> R.drawable.back_detail
        "traps" -> R.drawable.back_detail
        "triceps" -> R.drawable.tricep_detail
        "upper back" -> R.drawable.back_detail
        else -> R.drawable.core_detail
    }
}

fun formatMessageTimestamp(timestamp: Long): String {
    val now = Calendar.getInstance().timeInMillis
    Log.i("chatroom","now: $now")
    val diffMillis = abs(now - timestamp)
    Log.i("chatroom","diff: $diffMillis")
    val minuteMillis: Long = 60 * 1000
    val hourMillis: Long = 60 * minuteMillis
    val dayMillis: Long = 24 * hourMillis

    return when {
        diffMillis < minuteMillis -> "just now"
        diffMillis < 2 * minuteMillis -> "a minute ago"
        diffMillis < hourMillis -> "${diffMillis / minuteMillis} minutes ago"
        diffMillis < 2 * hourMillis -> "an hour ago"
        diffMillis < dayMillis -> "${diffMillis / hourMillis} hours ago"
        diffMillis < 2 * dayMillis -> "yesterday"
        else -> {
            val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

            val date = Date(timestamp)
            val formattedDate = dateFormat.format(date)
            val formattedTime = timeFormat.format(date)

            "$formattedDate"
        }
    }
}

