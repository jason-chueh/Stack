package com.example.stack.chatroom

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.stack.data.dataclass.Chatroom
import com.example.stack.data.dataclass.User
import com.example.stack.databinding.ItemChatroomBinding
import com.example.stack.databinding.ItemGymbroBinding
import com.example.stack.login.UserManager

class ChatroomAdapter(val onClick: (Chatroom)->Unit) :
    ListAdapter<Chatroom, ChatroomAdapter.ChatroomViewHolder>(DiffCallback) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatroomViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemChatroomBinding.inflate(layoutInflater, parent, false)
        return ChatroomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatroomViewHolder, position: Int) {
        val chatroom = getItem(position)
        holder.bind(chatroom)
    }

    inner class ChatroomViewHolder(private val binding: ItemChatroomBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(chatroom: Chatroom) {
            binding.chatroom = chatroom
            if(UserManager.user?.id == chatroom.userId1){
                binding.chatroomName.text = chatroom.userName[1]
            }
            else{
                binding.chatroomName.text = chatroom.userName[0]
            }
            binding.lastChat.text = chatroom.lastMessage
            binding.root.setOnClickListener{
                onClick(chatroom)
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Chatroom>() {
        override fun areItemsTheSame(oldItem: Chatroom, newItem: Chatroom): Boolean {
            return oldItem.roomId == newItem.roomId
        }

        override fun areContentsTheSame(oldItem: Chatroom, newItem: Chatroom): Boolean {
            return oldItem == newItem
        }
    }
}