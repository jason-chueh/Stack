package com.example.stack.chatroom

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import co.yml.charts.common.extensions.isNotNull
import com.example.stack.data.StackRepository
import com.example.stack.data.dataclass.Chat
import com.example.stack.data.dataclass.Chatroom
import com.example.stack.databinding.FragmentChatBinding
import com.example.stack.login.UserManager
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar
import javax.inject.Inject

@AndroidEntryPoint
class ChatFragment : Fragment() {

    lateinit var binding: FragmentChatBinding
    lateinit var chatroom: Chatroom

    @Inject
    lateinit var userManager: UserManager
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

        var sender: String
        var receiver: String
        var picture: String?
        if (userManager.user?.id == chatroom.userId1) {
            sender = chatroom.userId1 //user is the sender
            receiver = chatroom.userName[1]
            picture = chatroom.userPic[1]
        } else {
            sender = chatroom.userId2
            receiver = chatroom.userName[0]
            picture = chatroom.userPic[0]
        }

        viewModel.addListener(chatroom.roomId)

        binding = FragmentChatBinding.inflate(inflater, container, false)

        binding.imageUrl = picture

        binding.imageBack.setOnClickListener {
            findNavController().navigateUp()
        }

        val adapter = userManager.user?.id?.let { ChatAdapter(it, picture) }
        binding.chatName.text = receiver
        binding.chatRecyclerView.adapter = adapter
        binding.sendFrameLayout.setOnClickListener {
            val input = binding.input.text
            if (!input.isNullOrBlank()) {
                viewModel.sendMessage(
                    Chat(
                        chatId = "",
                        chatroomId = chatroom.roomId,
                        senderId = sender,
                        receiverId = receiver,
                        message = input.toString(),
                        sendTime = Calendar.getInstance().timeInMillis
                    )
                )
                viewModel.updateChatroom(
                    chatroom.copy(
                        lastMessage = input.toString(),
                        lastMessageTime = Calendar.getInstance().timeInMillis
                    )
                )
                binding.input.setText("")
            }
        }
        viewModel.chatList.observe(viewLifecycleOwner) {
            adapter?.submitList(it)
            val itemCount = adapter?.itemCount
            if (itemCount != null) {
                if (itemCount > 1) {
//                    binding.chatRecyclerView.scrollToPosition(itemCount - 1)
//                    val totalContentHeight = binding.chatRecyclerView.computeVerticalScrollRange()
//                    val recyclerViewHeight = binding.chatRecyclerView.height
//                    val bottomPosition = totalContentHeight - recyclerViewHeight
                    binding.chatRecyclerView.smoothScrollToPosition(it.size - 1)
//                    val layoutManager = binding.chatRecyclerView.layoutManager as LinearLayoutManager
//                    layoutManager.scrollToPositionWithOffset(itemCount - 1, 0)
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