package com.example.stack.plan

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.fragment.findNavController
import com.example.stack.NavigationDirections
import com.example.stack.R
import com.example.stack.databinding.FragmentHeightBinding
import com.example.stack.databinding.FragmentWeightBinding

class HeightFragment : Fragment() {
    lateinit var binding: FragmentHeightBinding

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHeightBinding.inflate(inflater, container, false)

        var monthPicker = binding.heightPicker

        monthPicker.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS

        monthPicker.wrapSelectorWheel = false
        monthPicker.textSize = 80f

        monthPicker.minValue = 140
        monthPicker.maxValue = 200
        monthPicker.value = 160
        binding.continueButton.setOnClickListener {
            findNavController().navigate(NavigationDirections.navigateToPlanTinderFragment())
        }

        return binding.root
    }
}