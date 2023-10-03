package com.example.stack.findbro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.stack.NavigationDirections
import com.example.stack.databinding.FragmentFindbroBinding
import com.example.stack.databinding.FragmentUserBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FindBroFragment: Fragment() {
    lateinit var binding: FragmentFindbroBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFindbroBinding.inflate(inflater, container, false)
        binding.textView2.setOnClickListener {
            findNavController().navigate(NavigationDirections.navigateToMapsFragment())
        }
        binding.textView3.setOnClickListener {
            findNavController().navigate(NavigationDirections.navigateToFindLocationFragment())
        }

        return binding.root
    }
}