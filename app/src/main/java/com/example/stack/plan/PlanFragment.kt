package com.example.stack.plan

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.chaquo.python.PyException
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.example.stack.NavigationDirections
import com.example.stack.data.network.PythonManager
import com.example.stack.databinding.FragmentPlanBinding
import com.example.stack.databinding.FragmentUserBinding
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi

class PlanFragment : Fragment() {
    lateinit var binding: FragmentPlanBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlanBinding.inflate(inflater, container, false)

        binding.textView.setOnClickListener {
            findNavController().navigate(NavigationDirections.navigateToExerciseDetailFragment())
        }




        return binding.root
    }
}








