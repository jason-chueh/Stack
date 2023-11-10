package com.example.stack.home.workout

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.stack.R
import com.example.stack.data.dataclass.ExerciseWithCheck
import com.example.stack.databinding.ItemExerciseBinding

//adapter for exercise list (dialog)
class ExerciseAdapter(val clickListener: (Int) -> Unit): ListAdapter<ExerciseWithCheck, ExerciseAdapter.ExerciseViewHolder>(
    DiffCallback
) {

    inner class ExerciseViewHolder(val binding: ItemExerciseBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(exercise: ExerciseWithCheck){
            binding.exercise = exercise
            if(exercise.check){

                val resolvedColor = ContextCompat.getColor(binding.root.context, R.color.secondaryContainer)
                binding.root.setBackgroundColor(resolvedColor)
                val textColor = ContextCompat.getColor(binding.root.context, R.color.black)
                binding.exerciseNameInDialog.setTextColor(textColor)

            }
            else{

                val resolvedColor = ContextCompat.getColor(binding.root.context, R.color.white)
                binding.root.setBackgroundColor(resolvedColor)

                val textColor = ContextCompat.getColor(binding.root.context, R.color.mediumBlack)
                binding.exerciseNameInDialog.setTextColor(textColor)

            }

            binding.exerciseNameInDialog.text = exercise.name
            binding.root.setOnClickListener{
                Log.i("filter","${exercise.name},  ${exercise.id}")
                clickListener(absoluteAdapterPosition)
                notifyItemChanged(absoluteAdapterPosition)
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

    companion object DiffCallback : DiffUtil.ItemCallback<ExerciseWithCheck>() {
        override fun areItemsTheSame(oldItem: ExerciseWithCheck, newItem: ExerciseWithCheck): Boolean {
            return oldItem.id == newItem.id
        }
        override fun areContentsTheSame(oldItem: ExerciseWithCheck, newItem: ExerciseWithCheck): Boolean {
            return oldItem == newItem
        }
    }
}