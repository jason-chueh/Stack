package com.example.stack.util

import java.util.Locale

fun <T> MutableList<T>.prepend(element: T) {
    add(0, element)
}
fun <T> MutableList<T>.swap(index1: Int, index2: Int) {
    if (index1 in 0 until size && index2 in 0 until size) {
        val temp = this[index1]
        this[index1] = this[index2]
        this[index2] = temp
    }
}


fun String.capitalizeFirstLetterOfWords(): String {
    return this.split(" ").joinToString(" ") { word ->
        word.replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(
            Locale.getDefault()
        ) else it.toString()
    } }
}

