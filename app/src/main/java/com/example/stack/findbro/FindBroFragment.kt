package com.example.stack.findbro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.stack.databinding.FragmentFindbroBinding
import com.example.stack.databinding.FragmentUserBinding

class FindBroFragment: Fragment() {
    lateinit var binding: FragmentFindbroBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFindbroBinding.inflate(inflater, container, false)



        return binding.root
    }
}