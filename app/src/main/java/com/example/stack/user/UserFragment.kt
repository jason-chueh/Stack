package com.example.stack.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.stack.databinding.FragmentHomeBinding
import com.example.stack.databinding.FragmentUserBinding
import com.example.stack.home.HomeViewModel
import com.example.stack.login.UserManager

class UserFragment: Fragment() {
    lateinit var binding: FragmentUserBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserBinding.inflate(inflater, container, false)

        binding.logout.setOnClickListener {
            UserManager.clear()
        }


        return binding.root
    }
}
