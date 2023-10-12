package com.example.stack.home.template

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.stack.data.dataclass.Template
import com.example.stack.databinding.ItemTemplateBrightBinding
import com.example.stack.databinding.ItemTemplateDarkBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class TemplateListAdapter(val onClick: (Template)->Unit) : ListAdapter<Template, RecyclerView.ViewHolder>(TemplateDiffCallback()) {

    companion object {
        private const val VIEW_TYPE_ODD = 1
        private const val VIEW_TYPE_EVEN = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_ODD -> {
                val binding = ItemTemplateDarkBinding.inflate(layoutInflater, parent, false)
                OddViewHolder(binding)
            }
            VIEW_TYPE_EVEN -> {
                val binding = ItemTemplateBrightBinding.inflate(layoutInflater, parent, false)
                EvenViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
            is OddViewHolder -> holder.bind(item)
            is EvenViewHolder -> holder.bind(item)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position % 2 == 0) {
            VIEW_TYPE_EVEN
        } else {
            VIEW_TYPE_ODD
        }
    }


    inner class OddViewHolder(private val binding: ItemTemplateDarkBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(template: Template) {
            binding.templateTitleText.text = template.templateName
            binding.dateText.text = formatTimestampToMonthDay(Calendar.getInstance().timeInMillis)
            binding.root.setOnClickListener {
                onClick(template)
            }
        }
    }

    inner class EvenViewHolder(private val binding: ItemTemplateBrightBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(template: Template) {
            binding.templateTitleText.text = template.templateName
            binding.dateText.text = formatTimestampToMonthDay(Calendar.getInstance().timeInMillis)
            binding.root.setOnClickListener {
                onClick(template)
            }
        }
    }

    // Implement TemplateDiffCallback to improve performance
    private class TemplateDiffCallback : DiffUtil.ItemCallback<Template>() {
        override fun areItemsTheSame(oldItem: Template, newItem: Template): Boolean {
            return oldItem.templateId == newItem.templateId
        }

        override fun areContentsTheSame(oldItem: Template, newItem: Template): Boolean {
            return oldItem == newItem
        }
    }
    fun formatTimestampToMonthDay(timestamp: Long): String {
        val date = Date(timestamp)
        val dateFormat = SimpleDateFormat("MMMM, d", Locale.getDefault())
        return dateFormat.format(date)
    }
}
