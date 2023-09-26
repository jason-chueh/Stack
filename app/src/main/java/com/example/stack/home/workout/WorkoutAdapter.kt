package com.example.stack.home.workout

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.stack.data.dataclass.ExerciseRecord
import com.example.stack.data.dataclass.ExerciseRecordWithCheck
import com.example.stack.data.dataclass.RepsAndWeights
import com.example.stack.data.dataclass.RepsAndWeightsWithCheck
import com.example.stack.databinding.ItemExerciseRecordBinding

//adapter for workout main page
class WorkoutAdapter(
    val clickListener: (name: String, id: String) -> Unit,
    val yesOnClick: (exercisePosition: Int, setPosition: Int, repsAndweights: RepsAndWeightsWithCheck) -> Unit,
    val noOnClick: (exercisePosition: Int, setPosition: Int, repsAndweights: RepsAndWeightsWithCheck) -> Unit,
    val addSetOnClick: (exercisePosition: Int)->Unit
) : ListAdapter<ExerciseRecordWithCheck, WorkoutAdapter.WorkoutViewHolder>(DiffCallback) {

    inner class WorkoutViewHolder(val binding: ItemExerciseRecordBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(exerciseRecord: ExerciseRecordWithCheck) {
            binding.exerciseName.text = exerciseRecord.exerciseName
            binding.exerciseName.setOnClickListener {
                clickListener(exerciseRecord.exerciseName, exerciseRecord.exerciseId)
            }
            binding.addSet.setOnClickListener {
                addSetOnClick(absoluteAdapterPosition)
                notifyItemChanged(absoluteAdapterPosition)
            }
            val adapter = WorkoutDetailAdapter(absoluteAdapterPosition, yesOnClick, noOnClick)
            binding.repsRecyclerView.adapter = adapter
            adapter.submitList(exerciseRecord.repsAndWeights)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemExerciseRecordBinding.inflate(layoutInflater, parent, false)
        return WorkoutViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        val exerciseRecord = getItem(position)
        holder.bind(exerciseRecord)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<ExerciseRecordWithCheck>() {
        override fun areItemsTheSame(oldItem: ExerciseRecordWithCheck, newItem: ExerciseRecordWithCheck): Boolean {
            Log.i("workout", "DiffCallbackCalled")
            return oldItem.exerciseId == newItem.exerciseId
        }

        override fun areContentsTheSame(oldItem: ExerciseRecordWithCheck, newItem: ExerciseRecordWithCheck): Boolean {
            return oldItem.repsAndWeights == newItem.repsAndWeights
        }
    }
}