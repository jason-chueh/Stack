package com.example.stack.home.template

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material.ExperimentalMaterialApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.stack.NavigationDirections
import com.example.stack.databinding.FragmentPlanTinderBinding
import com.example.stack.databinding.FragmentTemplateBinding

class TemplateFragment(): Fragment(){
    lateinit var binding: FragmentTemplateBinding
    //    private val viewModel by viewModels<TinderViewModel> { getVmFactory() }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTemplateBinding.inflate(inflater, container, false)
        binding.imageView4.setOnClickListener{
            findNavController().navigate(NavigationDirections.navigateToWorkoutFragment())
        }
        return binding.root
    }
}