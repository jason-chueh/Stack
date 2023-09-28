package com.example.stack.home.workout

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.stack.R
import com.example.stack.data.dataclass.RepsAndWeights
import com.example.stack.data.dataclass.RepsAndWeightsWithCheck
import com.example.stack.databinding.ItemSetBinding
import kotlin.math.abs

class WorkoutDetailAdapter(
    val exercisePosition: Int,
    val yesOnClick: (exercisePosition: Int, setPosition: Int, repsAndweights: RepsAndWeightsWithCheck) -> Unit,
    val noOnClick: (exercisePosition: Int, setPosition: Int, repsAndweights: RepsAndWeightsWithCheck) -> Unit,

    ) :
    ListAdapter<RepsAndWeightsWithCheck, WorkoutDetailAdapter.WorkoutDetailViewHolder>(DiffCallback) {


    companion object DiffCallback : DiffUtil.ItemCallback<RepsAndWeightsWithCheck>() {
        override fun areItemsTheSame(oldItem: RepsAndWeightsWithCheck, newItem: RepsAndWeightsWithCheck): Boolean {
            return (oldItem.reps == newItem.reps && oldItem.weight == newItem.weight)
        }

        override fun areContentsTheSame(oldItem: RepsAndWeightsWithCheck, newItem: RepsAndWeightsWithCheck): Boolean {
            return oldItem == newItem
        }
    }

    inner class WorkoutDetailViewHolder(val binding: ItemSetBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(repsAndWeights: RepsAndWeightsWithCheck) {
//            val isChecked = absoluteAdapterPosition in checkedPositions
            val isChecked = repsAndWeights.check

            binding.repsCount.isEnabled = !isChecked
            binding.kgCount.isEnabled = !isChecked

            if(isChecked){
                binding.root.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.lightOrange))
                binding.repsCount.setText(repsAndWeights.reps.toString())
                binding.kgCount.setText(repsAndWeights.weight.toString())
            }
            else{
                binding.root.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.white))
                binding.repsCount.setText("")
                binding.kgCount.setText("")
            }


            binding.setCount.text = (absoluteAdapterPosition + 1).toString()
            binding.yesButton.setOnClickListener {
                try {
                    Log.i("workout", "yes")
                    val reps = binding.repsCount.text.toString().toInt()
                    val kg = binding.kgCount.text.toString().toInt()

                    yesOnClick(exercisePosition, absoluteAdapterPosition, RepsAndWeightsWithCheck(reps, kg, true))
                    notifyItemChanged(absoluteAdapterPosition)
                } catch (e: Exception) {
                    Log.i("workout", "WorkoutDetailAdapter $e")
                }
            }
            binding.noButton.setOnClickListener {
                if(isChecked){
                    noOnClick(exercisePosition, absoluteAdapterPosition, repsAndWeights)
                    notifyItemChanged(absoluteAdapterPosition)
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutDetailViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemSetBinding.inflate(layoutInflater, parent, false)
        return WorkoutDetailViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WorkoutDetailViewHolder, position: Int) {
        val repsAndWeightsWithCheck = getItem(position)
        holder.bind(repsAndWeightsWithCheck)
    }
}