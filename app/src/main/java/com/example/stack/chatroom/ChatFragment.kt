package com.example.stack.chatroom

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import co.yml.charts.common.extensions.isNotNull
import com.example.stack.data.dataclass.Chat
import com.example.stack.data.dataclass.Chatroom
import com.example.stack.databinding.FragmentChatBinding
import com.example.stack.login.UserManager
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar

@AndroidEntryPoint
class ChatFragment : Fragment() {
    lateinit var binding: FragmentChatBinding
    lateinit var chatroom: Chatroom
    val viewModel: ChatViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        chatroom = ChatFragmentArgs.fromBundle(requireArguments()).chatroom

        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val adapter = UserManager.user?.id?.let { ChatAdapter(it) }
        var sender: String
        var receiver: String
        if(UserManager.user?.id == chatroom.userId1){
            sender = chatroom.userId1 //user is the sender
            receiver = chatroom.userId2
        }
        else{
            sender = chatroom.userId2
            receiver = chatroom.userId1
        }

        viewModel.addListener(chatroom.roomId)


        binding = FragmentChatBinding.inflate(inflater, container, false)
        binding.imageBack.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.chatName.text = receiver

        binding.chatRecyclerView.adapter = adapter
        binding.sendFrameLayout.setOnClickListener {
            val input = binding.input.text
            if(!input.isNullOrBlank()){
                viewModel.sendMessage(Chat(
                    chatId = "",
                    chatroomId = chatroom.roomId,
                    senderId = sender,
                    receiverId = receiver,
                    message = input.toString(),
                    sendTime = Calendar.getInstance().timeInMillis
                ))
                binding.input.setText("")
            }
        }
        viewModel.chatList.observe(viewLifecycleOwner){
            adapter?.submitList(it)
            val itemCount = adapter?.itemCount
            if (itemCount != null) {
                if(itemCount > 0){
                    binding.chatRecyclerView.scrollToPosition(itemCount - 1)
                }

            }
        }

        return binding.root
    }

    override fun onDestroy() {
        viewModel.removeListener()
        super.onDestroy()
    }
}