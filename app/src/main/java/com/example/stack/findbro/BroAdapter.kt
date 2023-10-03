package com.example.stack.findbro

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

import com.example.stack.data.dataclass.User
import com.example.stack.databinding.ItemGymbroBinding

class BroAdapter(val clickListener: (user: User) -> Unit) :
    ListAdapter<User, BroAdapter.BroViewHolder>(DiffCallback) {

    class OnClickListener(val clickListener: (user: User) -> Unit) {
        fun onClick(user: User) = clickListener(user)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BroViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemGymbroBinding.inflate(layoutInflater, parent, false)
        return BroViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BroViewHolder, position: Int) {
        val exercise = getItem(position)
        holder.bind(exercise)
    }

    inner class BroViewHolder(private val binding: ItemGymbroBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(user: User) {
            binding.user = user
            binding.root.setOnClickListener {
                clickListener(user)
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.id == newItem.id
        }
    }
}