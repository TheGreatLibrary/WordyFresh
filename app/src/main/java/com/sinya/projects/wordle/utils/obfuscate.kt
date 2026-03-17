package com.sinya.projects.wordle.utils

fun String.obfuscate(): String {
    val chars = "!*#@^$&~%?><"
    return this.map {
        if (it == ' ') ' '
        else chars.random()
    }.joinToString("")
}