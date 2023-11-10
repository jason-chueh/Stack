package com.example.stack.home.exercisedetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.stack.data.dataclass.ExerciseYoutube
import com.example.stack.databinding.ItemYoutubeBinding

class ExerciseDetailAdapter(val clickListener: (exercise: ExerciseYoutube) -> Unit) :
    ListAdapter<ExerciseYoutube, ExerciseDetailAdapter.ExerciseDetailViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseDetailViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemYoutubeBinding.inflate(layoutInflater, parent, false)
        return ExerciseDetailViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExerciseDetailViewHolder, position: Int) {
        val exercise = getItem(position)
        holder.bind(exercise)
    }

    inner class ExerciseDetailViewHolder(private val binding: ItemYoutubeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(exercise: ExerciseYoutube) {
            binding.exerciseYoutube = exercise
            binding.root.setOnClickListener {
                clickListener(exercise)
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<ExerciseYoutube>() {
        override fun areItemsTheSame(oldItem: ExerciseYoutube, newItem: ExerciseYoutube): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: ExerciseYoutube, newItem: ExerciseYoutube): Boolean {
            return oldItem.youtubeId == newItem.youtubeId
        }
    }
}
