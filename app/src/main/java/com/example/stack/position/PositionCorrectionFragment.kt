package com.example.stack.position

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.stack.databinding.FragmentPositionCorrectionBinding
import com.example.stack.databinding.FragmentUserBinding

class PositionCorrectionFragment: Fragment() {
    lateinit var binding: FragmentPositionCorrectionBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPositionCorrectionBinding.inflate(inflater, container, false)



        return binding.root
    }
}