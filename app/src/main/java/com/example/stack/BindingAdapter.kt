package com.example.stack

import android.annotation.SuppressLint
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.text.Editable
import android.text.Html
import android.text.Spanned
import android.text.style.ReplacementSpan
import android.transition.Transition
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.ImageViewTarget
import com.example.stack.util.capitalizeFirstLetterOfWords
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import org.xml.sax.XMLReader
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

@BindingAdapter("gifImageResource")
fun setGifImageResource(imageView: ImageView, resource: Int?) {
    resource?.let {
        val options = RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.DATA) // Cache GIF data
        Glide.with(imageView)
            .load(it)
            .apply(options)
            .into(imageView)
    }
}


//@BindingAdapter("imageUrl")
//fun loadImageUrl(view: ImageView, imageUrl: String?) {
//    imageUrl?.let {
//        Glide.with(view.context)
//            .load(imageUrl)
//            .into(view)
//    }
//}

@BindingAdapter("instructionList")
fun setInstructionList(textView: TextView, instructionList: List<String>) {
        val formattedText = StringBuilder()

        instructionList.forEachIndexed { index, instruction ->
            val stepTitle = "Step ${index + 1}"

            formattedText.append("<div>$stepTitle :</div>")

            formattedText.append(instruction)

            if(index != instructionList.size - 1){
                formattedText.append("<hr>")
            }
            else{
                formattedText.append("<br>")
            }
        }

        textView.text = Html.fromHtml(formattedText.toString(), Html.FROM_HTML_MODE_LEGACY, null, HtmlTagHandler())
}

@BindingAdapter("htmlText")
fun htmlText(textView: TextView, instruction: String?){
    if(instruction!=null){
        textView.text = Html.fromHtml(instruction, Html.FROM_HTML_MODE_LEGACY, null, HtmlTagHandler())
    }
}

@SuppressLint("ResourceAsColor", "ResourceType")
@BindingAdapter("chips", "mainChip")
fun setChips(chipGroup: ChipGroup, chipTextList: List<String>?, mainChip: String) {
    chipGroup.removeAllViews() // Clear existing chips
    val chipList = mutableListOf<String>()
    chipList.add(mainChip)
    if (chipTextList != null) {
        chipList.addAll(chipTextList)
    }
    val textColor = ContextCompat.getColor(chipGroup.context, R.color.white)
    val font = ResourcesCompat.getFont(chipGroup.context, R.font.rubik_medium)
    Log.i("chips","${chipList.size}")
    chipList.forEach { chipText ->
        val chip = Chip(chipGroup.context)
        chip.setChipBackgroundColorResource(R.color.primaryColor)
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

@BindingAdapter("capitalize")
fun capitalize(textView: TextView, inputText: String){
    textView.text = inputText.capitalizeFirstLetterOfWords()
}


@BindingAdapter("discardTemplateTitle")
fun discardTemplateTitle(textView: TextView, inputText: String?){
    inputText?.let{
        val outputText = "Discard " + it.capitalizeFirstLetterOfWords() + "?"
        textView.text = outputText
    }
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
    val diffMillis = abs(now - timestamp)
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

class HtmlTagHandler : Html.TagHandler {

    override fun handleTag(opening: Boolean, tag: String?, output: Editable?, xmlReader: XMLReader?) {
        if (output == null) return

        when (tag) {
            "hr" -> handleHrTag(opening, output)
            // Handle other tags if needed
        }
    }
    private fun handleHrTag(opening: Boolean, output: Editable) {
        val placeholder = "\n-\n" // Makes sure the HR is drawn on a new line
        if (opening) {
            output.insert(output.length, placeholder)
        } else {
            output.setSpan(HrSpan(), output.length - placeholder.length, output.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }
}
class HrSpan : ReplacementSpan() {

    override fun getSize(paint: Paint, text: CharSequence?, start: Int, end: Int, fm: Paint.FontMetricsInt?) = 0

    override fun draw(
        canvas: Canvas, text: CharSequence?, start: Int, end: Int, x: Float, top: Int, y: Int,
        bottom: Int, paint: Paint
    ) {
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 2f
        paint.color = Color.rgb(205,231,236)

        val interval = 20f
        val phase = 0f // Start with zero phase to ensure dots start from the beginning

        val pathEffect = DashPathEffect(floatArrayOf(interval, interval), phase)
        paint.pathEffect = pathEffect
        // Draw line in the middle of the available space
        val middle = ((top + bottom) / 2).toFloat()

        canvas.drawLine(0f, middle, canvas.width.toFloat(), middle, paint)
    }
}






