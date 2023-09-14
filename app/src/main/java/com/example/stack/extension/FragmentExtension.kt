package com.example.stack.extension

import androidx.fragment.app.Fragment
import com.example.stack.StackApplication
import com.example.stack.factory.ViewModelFactory

fun Fragment.getVmFactory(): ViewModelFactory {
    val repository = (requireContext().applicationContext as StackApplication).stackRepository
    return ViewModelFactory(repository)
}