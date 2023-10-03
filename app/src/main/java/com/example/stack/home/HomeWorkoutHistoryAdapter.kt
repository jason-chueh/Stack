package com.example.stack.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.stack.data.dataclass.Workout
import com.example.stack.databinding.ItemHomeWorkoutHistoryEvenBinding
import com.example.stack.databinding.ItemHomeWorkoutHistoryOddBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class HomeWorkoutHistoryAdapter :
    ListAdapter<Workout, RecyclerView.ViewHolder>(WorkoutDiffCallback()) {

    companion object {
        private const val VIEW_TYPE_ODD = 1
        private const val VIEW_TYPE_EVEN = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_EVEN -> {
                val binding = ItemHomeWorkoutHistoryEvenBinding.inflate(layoutInflater, parent, false)
                WorkoutViewHolderEven(binding)
            }
            VIEW_TYPE_ODD -> {
                val binding = ItemHomeWorkoutHistoryOddBinding.inflate(layoutInflater, parent, false)
                WorkoutViewHolderOdd(binding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val workout = getItem(position)
        when (holder) {
            is WorkoutViewHolderEven -> holder.bind(workout)
            is WorkoutViewHolderOdd -> holder.bind(workout)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position % 2 == 0) {
            VIEW_TYPE_EVEN
        } else {
            VIEW_TYPE_ODD
        }
    }

    inner class WorkoutViewHolderOdd(private val binding: ItemHomeWorkoutHistoryOddBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(workout: Workout) {
            binding.templateTitleText.text = workout.workoutName
            binding.dateText.text = formatTimestampToMonthDay(workout.startTime)
            binding.workoutDuration.text = formatTimeDuration(workout.endTime - workout.startTime)
        }
    }

    inner class WorkoutViewHolderEven(private val binding: ItemHomeWorkoutHistoryEvenBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(workout: Workout) {
            binding.templateTitleText.text = workout.workoutName
            binding.dateText.text = formatTimestampToMonthDay(workout.startTime)
            binding.workoutDuration.text = formatTimeDuration(workout.endTime - workout.startTime)

        }
    }


    class WorkoutDiffCallback : DiffUtil.ItemCallback<Workout>() {
        override fun areItemsTheSame(oldItem: Workout, newItem: Workout): Boolean {
            // Implement your logic to check if items are the same based on a unique identifier
            return oldItem.userId == newItem.userId && oldItem.startTime == newItem.startTime
        }
        override fun areContentsTheSame(oldItem: Workout, newItem: Workout): Boolean {
            // Implement your logic to check if the contents of items are the same
            return oldItem == newItem
        }
    }
    fun formatTimestampToMonthDay(timestamp: Long): String {
        val date = Date(timestamp)
        val dateFormat = SimpleDateFormat("MMMM, d", Locale.getDefault())
        return dateFormat.format(date)
    }
    fun formatTimeDuration(milliseconds: Long): String {
        val hours = (milliseconds / (1000 * 60 * 60)).toInt()
        val minutes = ((milliseconds % (1000 * 60 * 60)) / (1000 * 60)).toInt()

        val hoursText = if (hours > 0) "$hours h " else ""
        val minutesText = if (minutes > 0) "$minutes min" else ""

        return "$hoursText$minutesText"
    }
}
