package com.example.stack.plan

import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.stack.NavigationDirections
import com.example.stack.R
import com.example.stack.databinding.FragmentWeightBinding
import java.text.SimpleDateFormat
import java.util.Date

class InputFragment : Fragment() {
    lateinit var binding: FragmentWeightBinding

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWeightBinding.inflate(inflater, container, false)

        var monthPicker = binding.heightPicker

        monthPicker.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS

        monthPicker.wrapSelectorWheel = false
        monthPicker.textSize = 80f

        monthPicker.minValue = 40
        monthPicker.maxValue = 120
        monthPicker.value = 60


        monthPicker.setOnValueChangedListener { _, _, _ ->
        }
        binding.toggleButton.addOnButtonCheckedListener{toggleButton, checkedId, isChecked->
            if (isChecked) {
                when (checkedId) {
                    R.id.buttonKg->{
                        binding.unit.text = "Kg"
                    }
                    R.id.buttonLb->{
                        binding.unit.text = "Lb"
                    }
                }
            }
        }

        binding.continueButton.setOnClickListener {
            findNavController().navigate(NavigationDirections.navigateToHeightFragment())
        }
        return binding.root
    }
}