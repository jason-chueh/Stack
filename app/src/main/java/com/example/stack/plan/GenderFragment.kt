package com.example.stack.plan

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.stack.NavigationDirections
import com.example.stack.databinding.FragmentGenderBinding
import com.example.stack.databinding.FragmentWeightBinding

class GenderFragment: Fragment() {
    lateinit var binding: FragmentGenderBinding

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGenderBinding.inflate(inflater, container, false)

        binding.nextButton.setOnClickListener {
            findNavController().navigate(NavigationDirections.navigateToInputFragment())
        }





        return binding.root
    }
}