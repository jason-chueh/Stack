package com.example.stack.chatroom

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.stack.R
import com.example.stack.data.dataclass.Chat
import com.example.stack.databinding.ItemReceiveMessageBinding
import com.example.stack.databinding.ItemSendMessageBinding


private const val VIEW_TYPE_SENDER = 1
private const val VIEW_TYPE_RECEIVER = 2
class ChatAdapter(private val currentUserId: String) :
    ListAdapter<Chat, RecyclerView.ViewHolder>(ChatDiffCallback()) {

    override fun getItemViewType(position: Int): Int {
        val message = getItem(position)
        return if (message.senderId == currentUserId) {
            VIEW_TYPE_SENDER
        } else {
            VIEW_TYPE_RECEIVER
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_SENDER -> {
                val binding = ItemSendMessageBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                SenderViewHolder(binding)
            }
            VIEW_TYPE_RECEIVER -> {
                val binding = ItemReceiveMessageBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                ReceiverViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = getItem(position)
        when (holder) {
            is SenderViewHolder -> holder.bindSenderMessage(message)
            is ReceiverViewHolder -> holder.bindReceiverMessage(message)
        }
    }

    // Define your SenderViewHolder and ReceiverViewHolder classes here
    inner class SenderViewHolder(val binding: ItemSendMessageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindSenderMessage(chat: Chat) {
            binding.sendMessage.text = chat.message
            binding.time = chat.sendTime

        }
    }

    inner class ReceiverViewHolder(val binding: ItemReceiveMessageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindReceiverMessage(chat: Chat) {
            binding.receiveMessage.text = chat.message
            binding.time = chat.sendTime
        }
    }

    // Implement MessageDiffCallback to improve performance
    private class ChatDiffCallback : DiffUtil.ItemCallback<Chat>() {
        override fun areItemsTheSame(oldItem: Chat, newItem: Chat): Boolean {
            return oldItem.chatId == newItem.chatId
        }

        override fun areContentsTheSame(oldItem: Chat, newItem: Chat): Boolean {
            return oldItem == newItem
        }
    }
}
