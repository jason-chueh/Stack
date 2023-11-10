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
import com.example.stack.data.dataclass.RepsAndWeightsWithCheck
import com.example.stack.databinding.ItemSetBinding

class WorkoutDetailAdapter(
    val exercisePosition: Int,
    val yesOnClick: (exercisePosition: Int, setPosition: Int, repsAndweights: RepsAndWeightsWithCheck) -> Unit,
    val noOnClick: (exercisePosition: Int, setPosition: Int, repsAndweights: RepsAndWeightsWithCheck) -> Unit,
    val deleteOnClick: (exercisePosition: Int, setPosition: Int) -> Unit
) :
    ListAdapter<RepsAndWeightsWithCheck, WorkoutDetailAdapter.WorkoutDetailViewHolder>(DiffCallback) {

    companion object DiffCallback : DiffUtil.ItemCallback<RepsAndWeightsWithCheck>() {
        override fun areItemsTheSame(
            oldItem: RepsAndWeightsWithCheck,
            newItem: RepsAndWeightsWithCheck
        ): Boolean {
//            return false
            return (oldItem.reps == newItem.reps && oldItem.weight == newItem.weight)
        }

        override fun areContentsTheSame(
            oldItem: RepsAndWeightsWithCheck,
            newItem: RepsAndWeightsWithCheck
        ): Boolean {
//            return false
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
            val blue = ContextCompat.getColor(binding.root.context, R.color.mediumBlue)
            val black = ContextCompat.getColor(binding.root.context, R.color.black)

            if (isChecked) {
                binding.root.setBackgroundColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.secondaryContainer
                    )
                )
//                binding.repsCount.setText(repsAndWeights.reps.toString())
//                binding.kgCount.setText(repsAndWeights.weight.toString())
                binding.yesButton.setColorFilter(blue)
            } else {
                binding.root.setBackgroundColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.white
                    )
                )
//                binding.repsCount.setText("")
//                binding.kgCount.setText("")
                binding.yesButton.setColorFilter(black)
            }
            if (repsAndWeights.reps != 0) {
                binding.repsCount.setText(repsAndWeights.reps.toString())
            } else {
                binding.repsCount.setText("")
            }
            if (repsAndWeights.weight != 0) {
                binding.kgCount.setText(repsAndWeights.weight.toString())
            } else {
                binding.kgCount.setText("")
            }

            binding.setCount.text = (absoluteAdapterPosition + 1).toString()
            binding.yesButton.setOnClickListener {
                if (!binding.repsCount.text.isNullOrBlank() && !binding.kgCount.text.isNullOrBlank()) {
                    if (binding.repsCount.text.toString() != "0" && binding.kgCount.text.toString() != "0") {
                        if (!isChecked) {
                            Log.i("workout", "yes")
                            val reps = binding.repsCount.text.toString().toInt()
                            val kg = binding.kgCount.text.toString().toInt()
                            yesOnClick(
                                exercisePosition,
                                absoluteAdapterPosition,
                                RepsAndWeightsWithCheck(reps, kg, true)
                            )
                            notifyItemChanged(absoluteAdapterPosition)
                        } else {
                            noOnClick(exercisePosition, absoluteAdapterPosition, repsAndWeights)
                            notifyItemChanged(absoluteAdapterPosition)
                        }
                    }
                }
            }
            binding.noButton.setOnClickListener {
                deleteOnClick(exercisePosition, absoluteAdapterPosition)
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