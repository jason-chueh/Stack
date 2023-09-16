package com.example.stack.chatroom

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.stack.databinding.FragmentChatroomBinding
import com.example.stack.databinding.FragmentUserBinding

class ChatRoomFragment: Fragment() {
    lateinit var binding: FragmentChatroomBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatroomBinding.inflate(inflater, container, false)



        return binding.root
    }
}