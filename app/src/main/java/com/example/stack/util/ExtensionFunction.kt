package com.example.stack.util

import java.util.Locale

fun <T> MutableList<T>.prepend(element: T) {
    add(0, element)
}


fun String.capitalizeFirstLetterOfWords(): String {
    return this.split(" ").joinToString(" ") { word ->
        word.replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(
            Locale.getDefault()
        ) else it.toString()
    } }
}
