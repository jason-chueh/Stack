package com.example.stack.home.workout

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.stack.R
import com.example.stack.data.dataclass.Exercise
import com.example.stack.data.dataclass.User
import com.example.stack.databinding.ItemExerciseBinding
//adapter for exercise list (dialog)
class ExerciseAdapter(val clickListener: (Int) -> Unit): ListAdapter<Exercise, ExerciseAdapter.ExerciseViewHolder>(DiffCallback) {
    private var selectedPosition = RecyclerView.NO_POSITION
    private var previousSelectedPosition = RecyclerView.NO_POSITION
    inner class ExerciseViewHolder(val binding: ItemExerciseBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(exercise: Exercise){
            binding.exercise = exercise
            if(selectedPosition == absoluteAdapterPosition){

                val resolvedColor = ContextCompat.getColor(binding.root.context, R.color.lightOrange)
                binding.root.setBackgroundColor(resolvedColor)
                val textColor = ContextCompat.getColor(binding.root.context, R.color.black)
                binding.exerciseNameInDialog.setTextColor(textColor)

            }
            else{

                val resolvedColor = ContextCompat.getColor(binding.root.context, R.color.white)
                binding.root.setBackgroundColor(resolvedColor)

                val textColor = ContextCompat.getColor(binding.root.context, R.color.lightgrey)
                binding.exerciseNameInDialog.setTextColor(textColor)

            }

            binding.exerciseNameInDialog.text = exercise.name
            binding.root.setOnClickListener{

                clickListener(absoluteAdapterPosition)
                selectedPosition = absoluteAdapterPosition
                notifyItemChanged(selectedPosition)
                if(previousSelectedPosition != -1){
                    notifyItemChanged(previousSelectedPosition)
                }
                previousSelectedPosition = selectedPosition

            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemExerciseBinding.inflate(layoutInflater, parent, false)
        return ExerciseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val exerciseRecord = getItem(position)
        holder.bind(exerciseRecord)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Exercise>() {
        override fun areItemsTheSame(oldItem: Exercise, newItem: Exercise): Boolean {
            return oldItem.id == newItem.id
        }
        override fun areContentsTheSame(oldItem: Exercise, newItem: Exercise): Boolean {
            return oldItem == newItem
        }
    }
}