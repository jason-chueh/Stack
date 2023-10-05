package com.example.stack.chatroom

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.stack.NavigationDirections
import com.example.stack.databinding.FragmentChatroomBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatRoomFragment: Fragment() {
    lateinit var binding: FragmentChatroomBinding
    private val viewModel: ChatroomViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatroomBinding.inflate(inflater, container, false)
        viewModel.getChatroom()
        val adapter = ChatroomAdapter { chatroom ->
            findNavController().navigate(NavigationDirections.navigateToChatFragment(chatroom))
        }
        binding.chatroomRecyclerView.adapter = adapter
        viewModel.chatrooms.observe(viewLifecycleOwner){
            adapter.submitList(it.sortedByDescending { it.lastMessageTime })
        }
        return binding.root
    }
}