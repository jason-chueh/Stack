package com.example.stack.home.template

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.example.stack.NavigationDirections
import com.example.stack.R
import com.example.stack.databinding.DialogChooseTemplateBinding
import com.example.stack.databinding.DialogSaveTemplateBinding

class ChooseTemplateDialog: AppCompatDialogFragment() {
    lateinit var binding: DialogChooseTemplateBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.LoginDialog)
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT, // Set the desired width
            ViewGroup.LayoutParams.WRAP_CONTENT // Set the desired height
        )

        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogChooseTemplateBinding.inflate(inflater, container, false)

        binding.root.setOnClickListener {
            dialog?.dismiss()
        }
        binding.blankWorkout.setOnClickListener {
            findNavController().navigate(NavigationDirections.navigateToWorkoutFragment(null))
            dialog?.dismiss()
        }
        binding.templateWorkout.setOnClickListener {
            findNavController().navigate(NavigationDirections.navigateToTemplateFragment())
            dialog?.dismiss()
        }


        return binding.root
    }
}