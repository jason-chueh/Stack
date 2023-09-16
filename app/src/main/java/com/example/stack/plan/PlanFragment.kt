package com.example.stack.plan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.stack.databinding.FragmentPlanBinding
import com.example.stack.databinding.FragmentUserBinding

class PlanFragment: Fragment() {
    lateinit var binding: FragmentPlanBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlanBinding.inflate(inflater, container, false)



        return binding.root
    }
}