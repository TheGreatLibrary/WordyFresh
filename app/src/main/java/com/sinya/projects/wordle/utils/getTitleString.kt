package com.sinya.projects.wordle.utils

fun String.getTitleString(): String {
    return this.split(". ")
        .joinToString(". ") { it.replaceFirstChar { char -> char.uppercase() } }
}