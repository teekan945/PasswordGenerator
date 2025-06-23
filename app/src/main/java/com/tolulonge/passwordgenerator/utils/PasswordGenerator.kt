package com.tolulonge.passwordgenerator.utils

object PasswordGenerator {
    private const val LOWERCASE_CHARS = "abcdefghijklmnopqrstuvwxyz"
    private const val UPPERCASE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    private const val NUMBER_CHARS = "0123456789"
    private const val SYMBOL_CHARS = "!@#$%^&*()_-+=<>?/[]{}|"

    fun generatePassword(
        length: Int,
        includeUppercase: Boolean,
        includeLowercase: Boolean,
        includeNumbers: Boolean,
        includeSymbols: Boolean
    ): String {
        var charset = ""
        if (includeUppercase) charset += UPPERCASE_CHARS
        if (includeLowercase) charset += LOWERCASE_CHARS
        if (includeNumbers) charset += NUMBER_CHARS
        if (includeSymbols) charset += SYMBOL_CHARS

        if (charset.isEmpty()) return ""

        return (1..length)
            .map { charset.random() }
            .joinToString("")
    }
}