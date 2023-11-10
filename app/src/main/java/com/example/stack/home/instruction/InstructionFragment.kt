package com.example.stack.home.instruction

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.fragment.findNavController
import com.example.stack.data.dataclass.ExerciseYoutube
import com.example.stack.databinding.FragmentInstructionBinding
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InstructionFragment : Fragment() {
    lateinit var binding: FragmentInstructionBinding
    private val viewModel: InstructionViewModel by viewModels()
    lateinit var bundle: ExerciseYoutube
    lateinit var youtubePlayerListener: MyYouTubePlayerListener

    //    @Inject
//    lateinit var instructionViewModelFactory: InstructionViewModelFactory
//
//    private val viewModel: InstructionViewModel by viewModels {
//
//        instructionViewModelFactory.create(InstructionFragmentArgs.fromBundle(requireArguments()).youtubeKey)
//    }
    override fun onCreate(savedInstanceState: Bundle?) {
        bundle = InstructionFragmentArgs.fromBundle(requireArguments()).youtubeKey
        viewModel.getInstruction(bundle.youtubeId)
        youtubePlayerListener = MyYouTubePlayerListener(bundle.youtubeId)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInstructionBinding.inflate(inflater, container, false)


        val youTubePlayerView: YouTubePlayerView = binding.youtubePlayerView
        viewLifecycleOwner.lifecycle.addObserver(binding.youtubePlayerView)

        youTubePlayerView.addYouTubePlayerListener(
            youtubePlayerListener
        )

        binding.titleTextView.text = bundle.youtubeTitle

        binding.returnIcon.setOnClickListener {
            findNavController().navigateUp()
        }


        viewLifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onStop(owner: LifecycleOwner) {
                youTubePlayerView.removeYouTubePlayerListener(youtubePlayerListener)
            }
        })

        viewModel.instruction.observe(viewLifecycleOwner) {
            Log.i("instruction","$it")
            binding.instruction = it
        }
        return binding.root
    }


}

class MyYouTubePlayerListener(private var videoId: String) : AbstractYouTubePlayerListener() {
    override fun onReady(youTubePlayer: YouTubePlayer) {
        youTubePlayer.loadVideo(videoId, 0F)
    }
}

