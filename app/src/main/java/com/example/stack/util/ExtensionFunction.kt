package com.example.stack.util

fun <T> MutableList<T>.prepend(element: T) {
    add(0, element)
}

