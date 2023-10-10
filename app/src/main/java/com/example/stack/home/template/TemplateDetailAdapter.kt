package com.example.stack.home.template

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.stack.data.dataclass.TemplateExerciseRecord
import com.example.stack.databinding.ItemTemplateDetailBinding
import com.example.stack.util.capitalizeFirstLetterOfWords

class TemplateDetailAdapter : ListAdapter<TemplateExerciseRecord, TemplateDetailAdapter.TemplateViewHolder>(TemplateExerciseRecordDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TemplateViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemTemplateDetailBinding.inflate(inflater, parent, false)
        return TemplateViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TemplateViewHolder, position: Int) {
        val template = getItem(position)
        holder.bindTemplate(template)
    }

    inner class TemplateViewHolder(private val binding: ItemTemplateDetailBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindTemplate(template: TemplateExerciseRecord) {
            binding.exerciseNameText.text = template.exerciseName.capitalizeFirstLetterOfWords()
            binding.setNumber.text = template.repsAndWeights.size.toString()
        }
    }

    private class TemplateExerciseRecordDiffCallback : DiffUtil.ItemCallback<TemplateExerciseRecord>() {
        override fun areItemsTheSame(oldItem: TemplateExerciseRecord, newItem: TemplateExerciseRecord): Boolean {
            return oldItem.templateId == newItem.templateId && oldItem.exerciseId == newItem.exerciseId
        }

        override fun areContentsTheSame(oldItem: TemplateExerciseRecord, newItem: TemplateExerciseRecord): Boolean {
            return oldItem == newItem
        }
    }
}